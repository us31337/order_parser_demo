package service;

import model.OrderForOutput;
import model.StringWithLineFilename;

public interface OrderParser {

    OrderForOutput getOrderForOutput(StringWithLineFilename string);

}
