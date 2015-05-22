
package br.usp.icmc.poo.TurmaA015.TimeChecker;

import java.util.*;
import static java.util.Date.UTC;

public class TimeChecker {

    public String setDate(String date, int dayAmount){
        String[] parts = date.split("/");
        int day = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int year  = Integer.parseInt(parts[2]);
   
   
        while(checkDate(day, month, year, dayAmount)){
           
         
         if (month == 4 | month == 6|
                    month == 9| month == 11){
                if (dayAmount + day > 30){
                    dayAmount -= 30;
 
                    month++;
                   
                }
            }
 
            else if (month == 2){
 
                if (checkLeapYear(year)){
                    if (dayAmount + year > 29){
                        dayAmount -= 29;
 
                        month++;
                       
                    }
                }
                else{
                    if (dayAmount + day > 28){
                        dayAmount -= 28;
                       
                        month++;
                       
                    }
                }
            }
 
            else {
                if (dayAmount + day > 31){
                        dayAmount -= 31;
                        if (month == 12) {
                            year++;
                            month = 1;
                        }
                        else {
                            month++;
                        }
                }
 
            }
           
           
        }
 
            day+=dayAmount;
 
            return (day + "/" + month + "/" + year);
   
    }

    public int dateDifference(String dateA, String dateB){
        int dayIni, dayFin, monthIni, monthFin, yearIni, yearFin;
        int total = 0;
        
        String[] parts = dateA.split("/");

        dayIni = Integer.parseInt(parts[0]);
        monthIni = Integer.parseInt(parts[1]);
        yearIni = Integer.parseInt(parts[2]);

        parts = dateB.split("/");

        dayFin = Integer.parseInt(parts[0]);
        monthFin = Integer.parseInt(parts[1]);
        yearFin = Integer.parseInt(parts[2]);

        
        total += (int) yearIni*365;
        total -= (int) yearFin*365;
        total += dayIni;
        total -= dayFin;
       
       
        while (monthIni != 1){
       
            if (monthIni == 2 | monthIni == 4 | monthIni == 6 |
                      monthIni == 8 | monthIni == 9 | monthIni == 11)
                 total += 31;
           
            else if (monthIni == 3)
                total += 28;
           
            else if (monthIni != 1)
                total += 30;
 
            monthIni--;
        }
       
        while (monthFin != 1){
       
        if (monthFin == 2 | monthFin == 4 | monthFin == 6 |
                      monthFin == 8 | monthFin == 9 | monthFin == 11)
                 total -= 31;
           
            else if (monthFin == 3)
                total -= 28;
           
            else if (monthFin != 1)
                total -= 30;
       
            monthFin--;
        }
             
        total+=yearIni/4;
        total-=yearIni/100;
        total+=yearIni/400;
       
        total-=yearFin/4;
        total+=yearFin/100;
        total-=yearFin/400;
         
        return total;
    }
   
    public boolean checkDate(int day, int month, int year, int dayAmount){ // Serve para checar se o while do prazoFinal
   
        if (dayAmount > 31) return true;
       
        if (month == 4 | month == 6|
                            month == 9| month == 11)
                if (dayAmount > 30) return true;
       
        if (month == 2){
            if (checkLeapYear(year)){
                if (dayAmount > 29) return true;
            }
            else
                if (dayAmount > 28) return true;
        }
       
        return false;
   
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
}