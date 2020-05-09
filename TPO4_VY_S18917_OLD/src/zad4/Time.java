/**
 * @author Voiko Yehor S18917
 */

package zad4;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

public class Time {
    public static String passed(String from, String to) {
        if (from.contains(":")) {
            try {
                LocalDateTime ltFrom = LocalDateTime.parse(from);
                LocalDateTime ltTo = LocalDateTime.parse(to);
                String patt = "d MMMM yyyy (EEEE) 'godz.' HH:mm";
                Locale pl = new Locale("pl");
                String result = "Od " + ltFrom.format(DateTimeFormatter.ofPattern(patt, pl)) + " do "
                        + ltTo.format(DateTimeFormatter.ofPattern(patt,pl)) +"\n ";
                if (ChronoUnit.DAYS.between(ltFrom, ltTo) == 1)
                    result += "- mija: " + ChronoUnit.DAYS.between(ltFrom, ltTo) + " dzień, tygodni " + round(ChronoUnit.DAYS.between(ltFrom, ltTo) / 7.0,2);
                else
                    result += "- mija: " + ChronoUnit.DAYS.between(ltFrom, ltTo) + " dni, tygodni " + round(ChronoUnit.DAYS.between(ltFrom, ltTo) / 7.0,2);
                result+="\n - godzin: " + ChronoUnit.HOURS.between(ltFrom, ltTo) + ", minut: " + ChronoUnit.MINUTES.between(ltFrom, ltTo) + "\n ";
                result+="-kalendarzowo: ";
                Period period = Period.between(ltFrom.toLocalDate(), ltTo.toLocalDate());
                if(period.getYears()!=0){
                    if(period.getYears()==1){
                        result+=period.getYears() + " rok";
                    }
                    else if(period.getYears()>1 && period.getYears()<5){
                        result+=period.getYears() + " lata";
                    }
                    else {
                        result+=period.getYears() + " lat";
                    }
                }
                if(period.getMonths()!=0){
                    if(period.getYears()!=0)
                        result+=", ";
                    if(period.getMonths()==1){
                        result+=period.getMonths() + " miesiąc";
                    }
                    else if(period.getMonths()>1 && period.getMonths()<5){
                        result+=period.getMonths() + " miesiące";
                    }
                    else {
                        result+=period.getMonths() + " miesięcy";
                    }
                }
                if(period.getDays()!=0){
                    if(period.getYears()!=0 || period.getMonths()!=0)
                        result+=", ";
                    if(period.getDays()==1){
                        result+=period.getDays() + " dzień";
                    }
                    else {
                        result+=period.getDays() + " dni";
                    }
                }
                return result;
            } catch (Exception e) {
                return "*** " + e.toString();
            }
        } else {
            try {
                LocalDate ltFrom = LocalDate.parse(from);
                LocalDate ltTo = LocalDate.parse(to);
                Locale pl = new Locale("pl");
                String patt = "d MMMM yyyy (EEEE)";
                String result = "Od " + ltFrom.format(DateTimeFormatter.ofPattern(patt, pl)) + " do "
                        + ltTo.format(DateTimeFormatter.ofPattern(patt,pl)) +"\n ";
                if (ChronoUnit.DAYS.between(ltFrom, ltTo) == 1)
                    result += "- mija: " + ChronoUnit.DAYS.between(ltFrom, ltTo) + " dzień, tygodni " + round(ChronoUnit.DAYS.between(ltFrom, ltTo) / 7.0,2);
                else
                    result += "- mija: " + ChronoUnit.DAYS.between(ltFrom, ltTo) + " dni, tygodni " + round(ChronoUnit.DAYS.between(ltFrom, ltTo) / 7.0,2);
                result+="\n - kalendarzowo: ";
                Period period = Period.between(ltFrom, ltTo);
                if(period.getYears()!=0){
                    if(period.getYears()==1){
                        result+=period.getYears() + " rok";
                    }
                    else if(period.getYears()>1 && period.getYears()<5){
                        result+=period.getYears() + " lata";
                    }
                    else {
                        result+=period.getYears() + " lat";
                    }
                }
                if(period.getMonths()!=0){
                    if(period.getYears()!=0)
                        result+=", ";
                    if(period.getMonths()==1){
                        result+=period.getMonths() + " miesiąc";
                    }
                    else if(period.getMonths()>1 && period.getMonths()<5){
                        result+=period.getMonths() + " miesiące";
                    }
                    else {
                        result+=period.getMonths() + " miesięcy";
                    }
                }
                if(period.getDays()!=0){
                    if(period.getYears()!=0 || period.getMonths()!=0)
                        result+=", ";
                    if(period.getDays()==1){
                        result+=period.getDays() + " dzień";
                    }
                    else {
                        result+=period.getDays() + " dni";
                    }
                }
//                LocalDate ld = LocalDate.parse(period.toString());
//                System.out.println(ld.toString());
//                if(period.getYears()!=0)
                return result;
            } catch (Exception e) {
                return "*** " + e.toString();
            }

        }
    }

    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
