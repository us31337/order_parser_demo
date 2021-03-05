import com.fasterxml.jackson.databind.ObjectMapper;
import config.SpringConfig;
import model.OrderForOutput;
import model.StringWithLineFilename;
import org.apache.commons.io.FilenameUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import service.OrderParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class Main {

    private final ApplicationContext ctx;
    private ObjectMapper mapper;
    volatile private AtomicInteger howMuchFilesParsed = new AtomicInteger(0);

    public Main() {
        this.ctx = new AnnotationConfigApplicationContext(SpringConfig.class);
        this.mapper = ctx.getBean(ObjectMapper.class);
    }

    public static void main(String[] args) {
        final File[] files = checkArgs(args);
        File csv = files[0];
        File json = files[1];
        Main main = new Main();
        ArrayBlockingQueue<StringWithLineFilename> inputStrings = new ArrayBlockingQueue<>(10, true);
        ArrayBlockingQueue<OrderForOutput> result = new ArrayBlockingQueue<>(1, true);
        Runnable runCsvReadToQueue = () -> main.bufferReaderToList(csv, inputStrings);
        Runnable runJsonReadToQueue = () -> main.bufferReaderToList(json, inputStrings);
        Runnable runCsvSaveOrder = () -> main.readString(inputStrings, result);
        Runnable runShow = () -> main.showResult(result);
        Thread thread1 = new Thread(runCsvReadToQueue, "Поток записи из csv в очередь");
        Thread thread2 = new Thread(runJsonReadToQueue, "Поток записи из json в очередь");
        Thread thread3 = new Thread(runCsvSaveOrder, "Поток парсинга");
        Thread thread4 = new Thread(runShow, "Поток вывода результат");
        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
        try {
            thread1.join();
            thread2.join();
            thread3.join();
            thread4.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static File[] checkArgs(String[] args) {
        File[] files = new File[2]; //количество файлов по ТЗ меняться не должно, поэтому захардкожено
        if (args.length != 2) {
            showUsageAndExit();
        } else {
            files[0] = new File(args[0]);
            files[1] = new File(args[1]);
        }
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (!file.exists()) {
                throw new IllegalArgumentException("Файл " + args[i] + " не найден");
            }
        }
        return files;
    }

    private static void showUsageAndExit() {
        System.out.println("Для корректной работы программы требуются два параметра: наименование входного и выходного файлов");
        System.exit(0);
    }

    private void bufferReaderToList(File file,
                                    ArrayBlockingQueue<StringWithLineFilename> queue) {
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            int i = 1;
            while ((line = in.readLine()) != null) {
                StringWithLineFilename stringWithLineFilename = new StringWithLineFilename(line, file.getName(), i);
                queue.put(stringWithLineFilename);
                i++;
            }
            howMuchFilesParsed.addAndGet(1);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void readString(ArrayBlockingQueue<StringWithLineFilename> inputQueue,
                           ArrayBlockingQueue<OrderForOutput> resultQueue) {
        StringWithLineFilename string = null;
        int i = 0;
        while (howMuchFilesParsed.get() < 2 || resultQueue.size() > 0) {
            try {
                string = inputQueue.take();
                OrderParser parser = getParser(string.getFilename());
                OrderForOutput orderForOutput = parser.getOrderForOutput(string);
                resultQueue.offer(orderForOutput, 1, TimeUnit.SECONDS);
                i++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void showResult(ArrayBlockingQueue<OrderForOutput> result) {
        try {
            while (howMuchFilesParsed.get() < 2 || result.size() > 0) { //howMuchFilesParsed.get() < 2 && result.size() != 0
                OrderForOutput next = result.poll(1, TimeUnit.SECONDS);
                String endpoint = mapper.writeValueAsString(next);
                System.out.println(endpoint);
            }
        } catch (Exception e) {
//          на случай ошибки маппера или очереди, которые вряд ли возможны
            e.printStackTrace();
        }
    }

    private OrderParser getParser(String filename) {
        String extension = FilenameUtils.getExtension(filename);
        switch (extension.toLowerCase()) {
            case ("csv"):
                return ctx.getBean("csvOrderParser", OrderParser.class);
            case ("json"):
                return ctx.getBean("jsonOrderParser", OrderParser.class);
            default:
                throw new IllegalArgumentException("Unsupported file type");
        }
    }

}
