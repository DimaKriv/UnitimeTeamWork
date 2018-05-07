package toXmlParser.dataOptimization;

import java.util.ArrayList;
import java.util.HashMap;

public class ClassOptimizator {
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
    public ArrayList<Integer> countDatePattern( int[][] out, int index){
        ArrayList<Integer> dataptr= new ArrayList<>();
        int compOut = 0;
            for (int j= 0; j < out[index].length; j++) compOut += out[index][j];
        int sum = compOut;
        int interval = 16;
        for (int i= 1; i <= 16; i++) {
           sum = compOut * i;
            if (sum % 100 == 0 && sum > 100) {
                interval = i;
                break;
            }
        }
        dataptr.add((16 / interval) * interval);
        dataptr.add(interval);
        if (sum % 200 == 0)  {
            dataptr.add(200);
            dataptr.add(sum / 200);
        } else if (sum % 300 == 0)  {
            dataptr.add(300);
            dataptr.add(sum / 300);
        } else if (sum % 100 == 0)  {
            dataptr.add(100);
            dataptr.add(sum / 100);
        } else {
            dataptr.add(sum);
            dataptr.add(0);
        }
        return dataptr;
    }
    public String[] getDatePattern( ArrayList<Integer> countdata){
        // At future must be made by Database
        String [] database = new String[] {"Odd week;Even week,16,2,1","15 weeks/12 class,15,5,4"};
        String[][] data = new String[database.length][4];
        for (int i = 0; i < database.length; i++) {
            data[i] = database[i].split(",");
        }
        for (int i = 0; i < data.length; i++) {
            if (data[i][1].equals(countdata.get(0) + "")
                    && data[i][2].equals(countdata.get(1) + "") &&  data[i][3].equals(countdata.get(3) + "")) {
                return data[i][0].split(";");
            }
        }
        return null;
    }
    public String[] getTimePattern(ArrayList<Integer> out) {
        String[] patterns = new String[2];
        //No time pattern specified
        if (out.get(3) == 0) return null;
        int interval = out.get(1);
        int clas = out.get(3);
        if (interval > clas)  interval = clas;
        int mult = clas / interval;
        String pattern;
             if (clas > interval) {
                  pattern = (mult + 1) + "X" + (out.get(2) / 100 * 45) + "";
                  patterns[0] = pattern;
             } else patterns[0] = null;
             pattern = mult + "X" + (out.get(2) / 100 * 45) + "";
                 patterns[1] = pattern;
        return patterns;
    }
}
