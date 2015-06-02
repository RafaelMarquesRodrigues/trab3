package br.usp.icmc.poo.TurmaA015.MessageBundle;

import java.util.Locale;
import java.util.ResourceBundle;

public class MessageBundle {

        private final ResourceBundle bundle;
        
        private final Locale locale;
    
	public MessageBundle (String Language, String Country){
         
                locale = new Locale(Language, Country);
                    
                bundle = ResourceBundle.getBundle("MessageBundle", locale);
                
        }

	public String get(String message) {
            
		return bundle.getString(message);
                
	}

}