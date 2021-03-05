package service.impl;

import enums.MyCurrency;
import model.OrderForOutput;
import model.StringWithLineFilename;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import service.OrderParser;

@Component("csvOrderParser")
public class CsvOrderParserImpl implements OrderParser {

    @Value("${parser.csv.delimiter}")
    private String csvDelimiter;

    @Override
    public OrderForOutput getOrderForOutput(StringWithLineFilename string) {
        OrderForOutput output = new OrderForOutput();
        output.setFilename(string.getFilename());
        output.setLine(string.getLine());
        String content = string.getContent();
        String[] split = content.split(csvDelimiter);
        try {
            /*в обратном порядке чтобы записать побольше информации
            вероятность ошибки больше при парсинге чисел, так что они идут последними
            в ТЗ сказано, что все поля обязательны, так что прямое обращение к элементам массива не должно
            вызывать IndexOfBoundException*/
            String comment = split[3];
            output.setComment(comment);
            MyCurrency currency = MyCurrency.valueOf(split[2].toUpperCase());
            output.setCurrency(currency);
            int orderId = Integer.parseInt(split[0]);
            output.setOrderId(orderId);
            double price = Double.parseDouble(split[1]);
            output.setAmount(price);
            output.setResult("OK");
        } catch (Exception e) {
            String errMessage = e.getMessage();
            output.setResult("FAIL: " + errMessage);
        }
        return output;
    }
}
