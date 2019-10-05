import java.util.Scanner;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Collections;
import java.io.IOException;
import java.io.File;
import java.io.BufferedWriter;
import java.io.Writer;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class brick {
    //Main class name
    //public static final String MAIN_NAME = "brick"+".java";
    public static final String SUFFIX = ".tex";
    public static final String MODIFIED_SUFFIX = "_tuned.tex";
    public static final String DIR =  System.getProperty("user.dir")+"/";
    // add src for IDE
    //public static final String DIR =  System.getProperty("user.dir")+"/src";
    public static final String TEMP_NAME = "template.txt";
    //number of template, the last one is the solution block
    public static final int TEMPLATE_NUM = 4;

    public static void main(String []args)  throws IOException {
        //"Never write your homework from scratch"
        String file_name = get_target_file();
        //String file_name = "f19_629_hw4.tex";
        String output_file_name = file_name.substring(0,file_name.length()-4)+ MODIFIED_SUFFIX;
        List<List<String>> template = read_template(TEMP_NAME);
        //put target files in a list, read and output it to a separate file
        Path template_path = Paths.get(DIR, file_name);
        List<String> lines = Files.readAllLines(template_path, StandardCharsets.UTF_8);
        //Apply all the blocks, dont need to work on the last block
        int idx = 0;
        for(int i = 0; i < TEMPLATE_NUM - 1; i++){
            idx = apply_a_block(lines, template.get(i),idx);
            //System.out.println(template.get(i).get(0));
        }
        try{
            idx = apply_final_block(lines, template.get(template.size() - 1),idx);
        }catch(Exception e){
            System.out.println("Done!");
        }
        write_to_file(lines, output_file_name);
    }

    //--------Read current directory and get target file Method ----
    public static String get_target_file () throws IOException {
        System.out.println("-----Loading files-----");
        Scanner get_input = new Scanner(System.in);
        String folder_path = DIR;
        File[] files = new File(folder_path).listFiles();
        List<String> files_name = new ArrayList<>();
        //0 for file we need, 1 for output file name
        String file_name = "";
        //get all file names
        for(int i = 0; i < files.length; i++){
            String temp = files[i].getName();
            if(temp.endsWith(SUFFIX) && !temp.endsWith(MODIFIED_SUFFIX)){
                files_name.add(temp);
            }
        }
        //Normally we do homework from higher idx
        Collections.sort(files_name, Collections.reverseOrder());
        for(String s:files_name){
            System.out.print("Is that "+s+"?\ny/n ");
            String user_input =  get_input.nextLine();
            //let user choose the target file
            if(user_input.equals("y") || user_input.equals("Y")){
                file_name = s;
                //System.out.println("Received \"" +s + "\". Let's do this!");
                break;
            }
        }
        //raise error if no file match
        if(file_name == "" ){
            throw new java.lang.Error("\n\nNo file matched in [get_target_file] method. Looks like you are messing up with me.\n");
        }
        return file_name;
    }

    //------------------ Analysis template Method----------------------
    public static List<List<String>> read_template(String template_name) throws IOException {
        //the method read from a template file followed by a certain syntex
        //           and output a  two dimensional list with structure:
        //                     0 [block_name]
        //                     1 [keyword_for_detection]
        //                     2 [i for insertion/ r for replacement]
        //                     3 [insertion/replacement content]
        Path template_path = Paths.get(DIR, template_name);
        List<String> lines = Files.readAllLines(template_path, StandardCharsets.UTF_8);
        List<List<String>> template = new ArrayList<>();
        int temp_count = 0;
        for(int i = 0; i < lines.size(); i++){
            String ln = lines.get(i).trim();
            ln = ln.trim();
            //check if not comment or newline
            if(ln.length() == 0 || ln.length() > 1 && ln.substring(0,2).equals("//"))
                continue;
            //deal with one block
            List<String> block = new ArrayList<>();
            template.add(block);
            i = get_next_block(lines, block, i);
            //for(String s:block){
            //    System.out.println(s);
            //}
            //check if reach end
            temp_count++;
            if(temp_count == TEMPLATE_NUM){
                break;
            }

        }
        return template;
    }
    //pick a template block
    public static int get_next_block(List<String> lines,List<String> block, int idx){
        try{
            //name
            String name = lines.get(idx);
            block.add(name);
            //System.out.println("name: "+ name);

            //[keyword_for_detection]
            idx++;
            block.add(lines.get(idx));

            //[i for insertion/ r for replacement]
            idx++;
            if(lines.get(idx).equals("r")||lines.get(idx).equals("i"))
                block.add(lines.get(idx));
            else
                throw new java.lang.Error("Modify type not match. r/i expected");
            //insertion/replacement content
            idx++;
            String content = "";
            String end_str = "end_" + name;
            while(!lines.get(idx).equals(end_str)) {
                content += lines.get(idx) +"\n";
                idx++;
            }
            block.add(content);
        }catch (Exception e){
            throw new java.lang.Error("Yulk! The template file is poisoned ^q^");
        }
        return idx;
    }
    //------------------ Apply template Method----------------------
    public static int apply_a_block(List<String> lines, List<String> block, int idx){
        String key = block.get(1);
        String modify_type = block.get(2);
        String content = block.get(3);
        while(true){
            if(!lines.get(idx).trim().startsWith(key)){
                idx++;
                continue;
            }
            //System.out.println(lines.get(idx).trim());
            if(modify_type.equals("i")){
                lines.set(idx, content+lines.get(idx));
            }else{
                lines.set(idx, content);
            }
            idx++;
            break;
        }
        //System.out.println(idx);
        return idx;
    }
    //------------------ Apply final template Method!----------------------
    // Dangerous, will rise exception for sure
    //Oh Noooooooo! I'm repeating my code!
    public static int apply_final_block(List<String> lines, List<String> block, int idx){
        String key = block.get(1);
        String modify_type = block.get(2);
        String content = block.get(3);
        int question_num = 1;
        while(true){
            if(!(lines.get(idx).trim().startsWith(key) || lines.get(idx).trim().startsWith("\\end{questions}") )){
                idx++;
                continue;
            }
            //System.out.println(lines.get(idx).trim());
            if(modify_type.equals("i")){
                lines.set(idx,"%  Question "+ question_num+"\n" + content+lines.get(idx));
                question_num++;
            }else{
                lines.set(idx, content);
            }
            idx++;
        }
        //System.out.println(idx);
    }


    //------------------ Write to file Method----------------------
    public static void write_to_file(List<String> lines, String output_file_name){
        Writer writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(DIR+"/"+output_file_name), "utf-8"));
            for(String s:lines){
                writer.write(s+"\n");
            }
        } catch (IOException ex) {
            throw new java.lang.Error("Error in write_to_file method.");
        } finally {
            try {writer.close();} catch (Exception ex) {System.out.println("close nothing error");}
        }
    }




}

