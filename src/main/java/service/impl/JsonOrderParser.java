package service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import enums.MyCurrency;
import model.OrderForOutput;
import model.StringWithLineFilename;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import service.OrderParser;

@Component("jsonOrderParser")
public class JsonOrderParser implements OrderParser {
    private ObjectMapper mapper;

    @Autowired
    public JsonOrderParser(ObjectMapper mapper) {
        this.mapper = mapper;
    }


    @Override
    public OrderForOutput getOrderForOutput(StringWithLineFilename string) {
        OrderForOutput output = new OrderForOutput();
        output.setFilename(string.getFilename());
        output.setLine(string.getLine());
        String content = string.getContent();
        JsonNode jsonNode = null;
        try {
            jsonNode = mapper.readTree(content);
            /*в обратном порядке чтобы записать побольше информации
            вероятность ошибки больше при парсинге чисел, так что они идут последними*/
            String commentText = jsonNode.get("comment").textValue();
            output.setComment(commentText);
            String curr = jsonNode.get("currency").textValue();
            MyCurrency currency = MyCurrency.valueOf(curr.toUpperCase());
            output.setCurrency(currency);
            output.setOrderId(jsonNode.get("orderId").asInt());
            output.setAmount(jsonNode.get("amount").asDouble());
            output.setResult("OK");
        } catch (Exception e) {
            String errMessage = e.getMessage();
            output.setResult("FAIL: " + errMessage); //в ТЗ конечно написано просто указать причину ошибку,
            //но удобнее работать с результатом, когда кроме метки об успешном завершении есть еще метка для ошибок
        }
        return output;
    }
}
