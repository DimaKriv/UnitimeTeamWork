package toXmlParser.dataOptimization;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;

public class ClassOptimization {
    public static final String NO_DATE_PATTERN = "Undefined date pattern";
    public static final String NO_TIME_PATTERN = "Undefined time pattern";
    public int[] makeIntMassiveFromStringInput(String lec, String pra, String har) {
        int[] clasT = new int[3];
        String [] input = new String[]{lec.trim(), pra.trim(),har.trim()};
        for (int i = 0;i<input.length;i++) {
            if (input[i].indexOf(";")== -1) input[i] += "00";
            else {
                if (input[i].indexOf(";") == input[i].length() - 2) input[i] += "0";
                input[i] = input[i].replace(";","");
            }
            try {
                clasT[i]= Integer.parseInt(input[i]);
                if (clasT[i]< 0) clasT[i]=0;
            } catch (Exception exp) {
                clasT[i]=0;
            }
        }
        return clasT;
    }
    public boolean findPair(int index, int[][] out){
        for(int i = 1; i <out.length; i++) {
            if ((out[(index + i) % out.length][(index + i) % out.length] + out[index][index])%100 == 0) {
                out[index][(index + i) % out.length] = out[(index + i) % out.length][(index + i) % out.length];
                out[(index + i) % out.length][(index + i) % out.length] = 0;
                return true;
            }
        }
        return false;
    }

    public boolean findTPair(int[][] out){
        int sum = 0;
        for (int i = 0; i < out.length; i++) {
            sum += out[i][i];
        }
        if (sum % 100 == 0) {
            for (int i = 1; i < out.length; i++) {
                out[0][i] = out[i][i];
                out[i][i]=0;
            }
            return true;
        }
        return false;
    }

    public int[][] makeCourceOfferringData(int[] input) {
        int [][] out = new int[input.length][input.length];
        for (int i = 0; i < input.length; i++) {
            out[i][i]= input[i];
        }
        loop: for (int i = 0; i < input.length; i++) {
            if (out[i][i] % 100 != 0) if (findPair(i, out)) continue loop;
            if (out[i][i] % 100 != 0 && i == 0) {
                findTPair(out);
            }
        }
        return out;
    }
    public int[] countDatePattern(int[][] out, int index){
        int[] classInfo = new int[3];
        int compOut = 0;
            for (int acad100:out[index]) compOut += acad100;
        int acadHour100 = compOut;
        int interval = 16;
        for (int i= 1; i <= 16; i++) {
             acadHour100 = compOut * i;
            if (acadHour100 % 100 == 0 && acadHour100 > 100) {
                interval = i;
                break;
            }
        }
        int minOfClasses = (16 / interval) * acadHour100;
        int weeks=((16 / interval) * interval);
        classInfo[0] = weeks;
        if (acadHour100 % 200 == 0)  {
            classInfo[1]=200;
            classInfo[2] = minOfClasses / 200 ;
        } else if (acadHour100 % 300 == 0)  {
            classInfo[1]=300;
            classInfo[2] = minOfClasses / 300 ;
        } else if (acadHour100 % 100 == 0)  {
            classInfo[1]=100;
            classInfo[2] = minOfClasses / 100 ;
        } else {
            classInfo[1]=acadHour100;
            classInfo[2] = 0 ;
        }
        return classInfo;
    }
    public String getDatePattern(int[] configClass){
        // At future must be made by Database
        String[] database = new String[]{
                "16,100",
                "16,50",
        };
        HashMap<String, String> dataBaseWithPatternName = new HashMap<>();
        dataBaseWithPatternName.put(database[0], "Full Term");
        dataBaseWithPatternName.put(database[1], "Half Term");
        int weeks = configClass[0];
        int numberOfClasses = configClass[2];
        int procent =  (numberOfClasses * 100) / weeks;
        String checkPattern = findIfPatternSuit(database, weeks + "," + procent +"");
       if (checkPattern == null) return NO_DATE_PATTERN;
       return dataBaseWithPatternName.get(checkPattern);
    }

    public String getTimePattern(int[] configClass) {
        String[] dataBaseTimePattern = new String[] {
                "1X90", "2X90", "3X90", "1X135", "2X135"};
        String pattern;
        int[] numberAndMinutesOfClass = getNumberAndMinutesOfClass(configClass);
        if (numberAndMinutesOfClass == null) return NO_TIME_PATTERN;
             pattern = numberAndMinutesOfClass[0] + "X" + numberAndMinutesOfClass[1];
           String validPattern = findIfPatternSuit(dataBaseTimePattern, pattern);
         if (pattern == null) validPattern = NO_TIME_PATTERN;
        return validPattern;
    }

    public String findIfPatternSuit(String[] database, String pattern) {
       Optional<String> isValidPattern  = Arrays.asList(database).stream()
               .filter(validPattern -> validPattern.equals(pattern)).findAny();
       if (isValidPattern.isPresent()) return pattern;
       else return null;
    }

    public int[] getNumberAndMinutesOfClass(int[] configClass) {
        int minutesInAcademicHours = 45;
        int numberOfClasses = configClass[2];
        int classAcademicHoursMult100 = configClass[1];
        int weeks = configClass[0] ;
        if (numberOfClasses == 0) return null;
            if (weeks > numberOfClasses)  weeks = numberOfClasses;
        int classesAtWeek = numberOfClasses / weeks;
        int[] output = new int[]{classesAtWeek,(classAcademicHoursMult100 / 100 * minutesInAcademicHours)};
        return output;
    }
}
