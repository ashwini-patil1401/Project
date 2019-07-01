import java.io.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.HashMap;

class Column{
    
    HashMap<String,Integer> column_name_list = new HashMap<String,Integer>();
    Column(LinkedList<String> col){
        ListIterator<String> iterate = col.listIterator() ;
        while(iterate.hasNext()){
            column_name_list.put(iterate.next(),1);
        }
    }
}

class ReadFile {
    
    HashMap<String, Column> hmap = new HashMap<String, Column>();
    String inputfile; 
    String outputfile;
    
    ReadFile(String inputfile, String outputfile){
        this.inputfile = inputfile;
        this.outputfile = outputfile;
    }
    
    int search(String s, String file){
        
        int count = 0;
        String word[] = file.split("[\\s\"(),;]+");
        for(int i = 0;i < word.length;i++){
            if(word[i].contains(s))
            count++;
        }
        return count;
    }
    
    /*Function reads file and appends suggestions in the form of comments for fast query processing */
    LinkedList<String>  optimize( LinkedList<String>  file){
        
        String sCurrentLine="";
        ListIterator<String> iterate = file.listIterator() ;
        String table_name = new String();
        LinkedList<String> columns = new LinkedList<String>();
        String query = new String();
        String col = "";
        String suggest = "";
        LinkedList<String> newfile = new LinkedList<String>();
        
        while(iterate.hasNext()){
            sCurrentLine = iterate.next();
            newfile.add(sCurrentLine);
            if((sCurrentLine = sCurrentLine.toLowerCase()).contains("select")){
                
                String array[] = sCurrentLine.split("[\\s\"(),;]+");
                int  i = 0;
                
                while(i< array.length){
                    query = "";
                    suggest = "";
                    if(array[i].equals("select")){
                        query = query.concat("\""+array[i]+" ");
                        /*Search the columns*/
                        while(!array[++i].equals("from")) {
                            columns.add(array[i]);
                        }
                        /*Get the table_name*/
                        table_name = array[++i];
                        
                        if(++i < array.length){
                            do{
                                if(array[i].equals("where")){
                                    col = array[++i];
                                    suggest = ("<!--").concat("creating index could be useful on column:"+col+"-->");
                                    newfile.add(suggest);
                                    System.out.println("\nquery:"+sCurrentLine);
                                    System.out.println("index:"+suggest);
                                }
                                else if(array[i].equals("select")){
                                    suggest = ("<!--").concat("Using join on "+col+" instead of nested select can help fasten"+col+"-->");
                                    newfile.add(suggest);
                                    System.out.println("\nquery:"+sCurrentLine);
                                    System.out.println("join:"+suggest);
                                }
                                i++;
                            }while(i<array.length);
                        }
                        else{
                            /*Look for table_name in hashmap*/
                            /*If table not present in map, add it*/
                            if(hmap.isEmpty()) {
                                hmap.put(table_name,new Column(columns));
                            }
                            /*If table already present see if query can be optimized*/
                            else if (hmap.containsKey(table_name)) {
                                ListIterator<String> iterate_col = columns.listIterator();
                                String temp = "" ; int count = 0;
                                
                                while(iterate_col.hasNext() ){
                                    temp = iterate_col.next();
                                    
                                    if((hmap.get(table_name).column_name_list).get(temp)== null){
                                        count = search(temp,file.toString());
                                        (hmap.get(table_name).column_name_list).put(temp,count);
                                        
                                        if(count <= 1){
                                            suggest = "<!-- Column "+temp+" is not used anywhere and can be removed -->";
                                            System.out.println("\nquery:"+sCurrentLine);
                                            System.out.println("count:"+suggest);
                                        }
                                    }
                                }
                                col = hmap.get(table_name).column_name_list.keySet().toString().substring(1,(hmap.get(table_name).column_name_list.keySet().toString().length()-1));
                                query = query.concat(col +" from ");
                                query = query.concat(" "+table_name);
                                suggest = ("<!-- ").concat("Instead first query can be:"+query+"\";"+". This extra query can be merged-->");
                                System.out.println("\nquery:"+sCurrentLine);
                                System.out.println("sug:"+suggest);
                                newfile.add(suggest);
                            }
                        }/*end of if-select*/
                        
                        
                    }
                    i++;
                }/*end of while for select statement analysis*/
            }
            
        }/*end of while */
        
        return newfile;
    }
    
    void read() {
        LinkedList<String> final_file = new LinkedList<String>();
        String sCurrentLine = "";
        
        try (BufferedReader br = new BufferedReader(new FileReader(inputfile))) {
            
            while ((sCurrentLine = br.readLine()) != null) {
                final_file.add(sCurrentLine);
            }
            final_file = optimize(final_file);
            write(final_file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    void write(LinkedList<String> final_file){
        String newLine = System.getProperty("line.separator");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputfile))) {
            ListIterator<String> iterate = final_file.listIterator();
            while (iterate.hasNext()){
                bw.write(iterate.next());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

public class Optimize_JSDatabaseSQL {
    public static void main(String args[]){
        ReadFile rf =  new ReadFile(args[0], args[1]);
        rf.read();
    }
}

/*
*
query:        $query = "select sex,location from yourtable";
sug:<!-- Instead first query can be:"select address, sex, name, location, age from  yourtable";. This extra query can be merged-->

query:	$query = "select sex,location from mytable where sex = 'female'";
index:<!--creating index could be useful on column:sex-->

query:	$query = "select sex,location from mytable where age in (select age from kids)";
index:<!--creating index could be useful on column:age-->

query:	$query = "select sex,location from mytable where age in (select age from kids)";
join:<!--Using join on age instead of nested select can help fastenage-->

* */