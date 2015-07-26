/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Service;

import ProductDetails.Product;
import ProductDetails.ListOfProducts;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.json.Json;
import javax.json.JsonObject;

/**
 *
 * @author c0652863
 */
@MessageDriven(mappedName = "jms/Queue")
public class ProductListener implements MessageListener {
    
    @Inject
 ListOfProducts ListOfProducts;
    
    @Override
    public void onMessage(Message message) {
        try{
            if(message instanceof TextMessage){
                String jsonString = ((TextMessage) message).getText();
                JsonObject JSONObject = Json.createReader(new StringReader(jsonString)).readObject();
                ListOfProducts.add(new Product(JSONObject));
            }
        } catch(JMSException ex){
            System.err.println("JMS Failure");
        } catch(Exception ex){
            Logger.getLogger(ProductListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
