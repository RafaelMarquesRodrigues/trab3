
package br.usp.icmc.poo.TurmaA015.TimeChecker;

import java.util.*;
import static java.util.Date.UTC;

public class TimeChecker {

    @SuppressWarnings( "deprecation" )
	public int calculateDifference(String initialStringDate, String finalStringDate) {
     
            String[] initialDateNumbers;
            String[] finalDateNumbers;
            Date initialDate;
            Date finalDate;
            float difference;
 
           
            initialDateNumbers = initialStringDate.split("/");
            finalDateNumbers = finalStringDate.split("/");
           
            initialDate = new Date (Integer.parseInt(initialDateNumbers[0]) - 1900,
                                            Integer.parseInt(initialDateNumbers[1]),
                                            Integer.parseInt(initialDateNumbers[2]));
           
            finalDate = new Date (Integer.parseInt(finalDateNumbers[0]) - 1900,
                                            Integer.parseInt(finalDateNumbers[1]),
                                            Integer.parseInt(finalDateNumbers[2]));
           
            difference = UTC (initialDate.getYear(),
                                initialDate.getMonth(),
                                initialDate.getDate(),
                                initialDate.getDay(),
                                initialDate.getHours(),
                                initialDate.getSeconds())
                   
                   
                                          -
                   
                         UTC (finalDate.getYear(),
                                finalDate.getMonth(),
                                finalDate.getDate(),
                                finalDate.getDay(),
                                finalDate.getHours(),
                                finalDate.getSeconds());
           
 
            return (int) difference/86400000; //transformar de milisegundos para dias
    }   //valores positivos representam praso restante, valores negativos representam atraso

    @SuppressWarnings( "deprecation" )
    public String setDate(String initialStringDate, int dayAmount){   //muda esse nome
           
        String[] initialDateNumbers;
        initialDateNumbers = initialStringDate.split("/");
           
        Date date = new Date (Integer.parseInt(initialDateNumbers[0]) - 1900,
                                            Integer.parseInt(initialDateNumbers[1]),
                                            Integer.parseInt(initialDateNumbers[2]));
           
        while(checkDate(date, dayAmount)){  
            if (date.getMonth() == 4 | date.getMonth() == 6|
                    date.getMonth() == 9| date.getMonth() == 11){
                if (dayAmount + date.getDate() > 30){
                    dayAmount -= 30;
 
                    date.setMonth(date.getMonth() + 1);
                   
                }
            }
 
            else if (date.getMonth() == 2){
 
                if (checkLeapYear(date.getYear() + 1900)){
                    if (dayAmount + date.getDate() > 29){
                        dayAmount -= 29;
 
                        date.setMonth(date.getMonth() + 1);
                       
                    }
                }
                else{
                    if (dayAmount + date.getDate() > 28){
                        dayAmount -= 28;
                       
                        date.setMonth(date.getMonth() + 1);
                       
                    }
                }
            }
 
            else {
                if (dayAmount + date.getDate() > 31){
                        dayAmount -= 31;
                        if (date.getMonth() == 12) {
                            date.setYear(date.getYear() + 1);
                            date.setMonth(1);
                        }
                        else {
                            date.setMonth(date.getMonth() + 1);
                        }
                }
 
            }
           
           
        }
 
        date.setDate(date.getDate() + dayAmount);
 
        return (date.getDate() + "/" + date.getMonth() + "/" + (date.getYear() + 1900));
    }
    
    public boolean dateCondition(int day, int month, int year){
       
        if (day > 31) return false;
        if (day < 1) return false;
        if (month > 12) return false;
        if (month < 1) return false;
        if (month == 4 | month == 6| month == 9| month == 11)
                if (day > 30) return false;
        if (month == 2){
            if (checkLeapYear(year)){
                if (day > 29) return false;
            }
            else
                if (day > 28) return false;
        }
           
        return true;
    }

    private boolean checkLeapYear(int year){
        if (year % 4 != 0) return false;
        else if (year % 100 != 0) return true;
        else return (year % 400 == 0);
    }
    
    @SuppressWarnings( "deprecation" )         
    public boolean checkDate(Date date, int dayAmount){ // Serve para checar se o while do prazoFinal
   
        if (dayAmount > 31) return true;
       
        if (date.getMonth() == 4 | date.getMonth() == 6|
                            date.getMonth() == 9| date.getMonth() == 11)
                if (dayAmount > 30) return true;
       
        if (date.getMonth() == 2){
            if (checkLeapYear(date.getYear())){
                if (dayAmount > 29) return true;
            }
            else
                if (dayAmount > 28) return true;
        }
       
        return false;
   
    }
}