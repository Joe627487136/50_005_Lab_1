package com.example;

import java.io.*;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class SimpleShell {
    public static void main(String[] args) throws java.io.IOException {

        //////global variables building/////
        String commandLine;
        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
        ProcessBuilder pb = new ProcessBuilder();
        List<String> history = new ArrayList<String>();
        int index = 0;
        File dir = null;
        List<String> hcom = new ArrayList<String>();
        boolean hisopen = false;
        //////finish global variables building/////



        /////Enter while loop for jsh> bash terminal///////
        while (true) {
            List<String> commandList = new ArrayList<String>();
            System.out.print("jsh>");
            commandLine = console.readLine();
            history.add(commandLine);


            ////Ignore blank input/////
            if (commandLine.equals("")) {
                hisopen = false;
                continue;
            }


            //////Use isBlank() to double check blank input////
            if (isBlank(commandLine)){
                hisopen = false;
                continue;
            }


            //////Generate a list of commands called commandslist////
            String[] input = commandLine.trim().split(" ");
            for (int i = 0; i < input.length; i++) {
                commandList.add(input[i]);
            }


            //////Ignore blank input which caused commandlist blank/////
            if (commandList.size()==0){
                hisopen = false;
                continue;
            }


            //////Check if input is "history"///////
            //////Create history list/////
            if (commandLine.trim().equals("history")) {
                for(String s : history){
                    System.out.println((index++) + " " + s);
                }
                index = 0;
                hisopen = true;
                continue;
            }


            //////Check if input is a int and history has been opened already/////
            //////hisopen is boolean value for checking if history opened or not//////
            //////Do functions as recall commands by history list index and exexcute/////
            if (isInteger(commandLine.trim())==true && hisopen==true){
                if (Integer.valueOf(commandLine.trim())<=history.size()){
                    if (history.get(Integer.valueOf(commandLine.trim())).trim().equals("history")){
                    System.out.println(history.get(Integer.valueOf(commandLine.trim())));
                    history.set(history.size()-1,history.get(Integer.valueOf(commandLine.trim())));
                    for(String s : history){
                        System.out.println((index++) + " " + s);
                    }
                    index = 0;
                    hisopen = true;
                    continue;
                    }
                    try {
                        String out = "";
                        System.out.println(history.get(Integer.valueOf(commandLine.trim())));
                        String Innercommand = history.get(Integer.valueOf(commandLine.trim()));
                        try {
                            List<String> icmdl = new ArrayList<String>();
                            String[] Scmd = Innercommand.trim().split(" ");
                            if (Scmd.length==0||Innercommand==""||isBlank(Innercommand)){
                                history.set(history.size() - 1, history.get(Integer.valueOf(commandLine.trim())));
                                continue;
                            }
                            for (int i = 0; i < Scmd.length; i++) {
                                icmdl.add(Scmd[i]);
                            }
                            if (icmdl.contains("cd")) {
                                if (icmdl.get(icmdl.size() - 1).equals("cd")) {
                                    continue;
                                }
                                else if (icmdl.get(icmdl.size() - 1).equals("..")) {
                                    File pdir = new File(System.getProperty("user.dir")).getParentFile().getAbsoluteFile();
                                    System.setProperty("user.dir", pdir.getAbsolutePath());
                                    System.out.println(pdir);
                                    dir = pdir;
                                    continue;
                                }
                                else {
                                    File cdir = new File(System.getProperty("user.dir")).getAbsoluteFile();
                                    String des = icmdl.get(1);
                                    File ndir = new File(cdir+"/"+des);
                                    boolean exists = ndir.exists();


                                    if(exists){
                                        System.setProperty("user.dir", ndir.getAbsolutePath());
                                        System.out.println(ndir);
                                        dir = ndir;
                                        continue;
                                    }
                                    else{
                                        System.out.println("error: new directory is invalid!"+ ndir);
                                        continue;
                                    }
                                }
                            }
                            out = icmdl.get(0).trim();
                        }catch (Exception e){
                            out = Innercommand.trim();
                        }
                        if (Innercommand.trim().equals("history")){
                            out = Innercommand.trim();
                        }
                    pb.command(out);
                    history.set(history.size() - 1, history.get(Integer.valueOf(commandLine.trim())));
                    Process process = pb.start();
                    InputStream is = process.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader br = new BufferedReader(isr);
                    String line;
                    while ((line = br.readLine()) != null)
                        System.out.println(line);
                    br.close();
                    continue;
                    }
                    catch (Exception e){
                        System.out.println("Cannot run program " + (char)34 + history.get(Integer.valueOf(commandLine.trim())) + (char)34 + " (in directory " + (char)34 + System.getProperty("user.dir") + (char)34+"): " + "error=2, No such file or directory");
                        continue;
                    }
                }
                else {
                    System.out.println("Invalid command history search!");
                    continue;
                }
            }


            ///////Check if "cd" input//////
            ///////Check for ".." or directory input and execute//////
            if (commandList.contains("cd")) {
                if (commandList.get(commandList.size() - 1).equals("cd")) {
                    hisopen = false;
                    continue;
                }
                else if (commandList.get(commandList.size() - 1).equals("..")) {
                    File pdir = new File(System.getProperty("user.dir")).getParentFile().getAbsoluteFile();
                    System.setProperty("user.dir", pdir.getAbsolutePath());
                    System.out.println(pdir);
                    dir = pdir;
                    hisopen = false;
                    continue;
                }
                else {
                    File cdir = new File(System.getProperty("user.dir")).getAbsoluteFile();
                    String des = commandList.get(1);
                    File ndir = new File(cdir+"/"+des);
                    boolean exists = ndir.exists();
                    hisopen = false;


                    if(exists){
                        System.setProperty("user.dir", ndir.getAbsolutePath());
                        System.out.println(ndir);
                        dir = ndir;
                        continue;
                    }
                    else{
                        System.out.println("error: new directory is invalid!"+ ndir);
                        continue;
                    }
                }
            }


            //////Check if input is "!!"/////
            /////Recall history for previous one command and execute////
            if(commandLine.trim().equals("!!")){
                hisopen = false;
                try {
                    if (history.get(history.size()-2).equals("cd ..")){
                        history.set(history.size()-1,history.get(history.size()-2));
                        File pdir = new File(System.getProperty("user.dir")).getParentFile().getAbsoluteFile();
                        System.setProperty("user.dir", pdir.getAbsolutePath());
                        System.out.println(pdir);
                        dir = pdir;
                        hisopen = false;
                        continue;
                    }

                    if (history.get(history.size()-2).contains("cd")){
                        history.set(history.size()-1,history.get(history.size()-2));
                        File cdir = new File(System.getProperty("user.dir")).getAbsoluteFile();
                        String des = commandList.get(1);
                        File ndir = new File(cdir+"/"+des);
                        boolean exists = ndir.exists();
                        hisopen = false;


                        if(exists){
                            System.setProperty("user.dir", ndir.getAbsolutePath());
                            System.out.println(ndir);
                            dir = ndir;
                            continue;
                        }
                        else{
                            //String output = haha + "/" + dir;
                            System.out.println("error: new directory is invalid!"+ ndir);
                            continue;
                        }
                    }
                }catch (Exception e){
                    String[] k = history.get(history.size()-1).split(" ");
                    for (int i = 0; i < k.length; i++) {
                        hcom.add(k[i]);
                    }
                    File cdir = new File(System.getProperty("user.dir")).getAbsoluteFile();
                    String des = hcom.get(1);
                    File ndir = new File(cdir+"/"+des);
                    System.out.println("error: new directory is invalid!"+ ndir);
                    continue;
                }

                try {

                    if (history.get(history.size()-2).equals("history")){
                        System.out.println(history.get(history.size()-2));
                        history.set(history.size()-1,history.get(history.size()-2));
                        for(String s : history){
                            System.out.println((index++) + " " + s);
                        }
                        index = 0;
                        hisopen = true;
                        continue;
                    }
                    System.out.println(history.get(history.size()-2));
                    pb.command(history.get(history.size()-2));
                    history.set(history.size()-1,history.get(history.size()-2));
                    Process process = pb.start();
                    InputStream is = process.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader br = new BufferedReader(isr);
                    String line;
                    while((line = br.readLine()) != null)
                        System.out.println(line);
                    br.close();
                    continue;
                }
                catch (Exception e){
                    System.out.println("error: no history to recall");
                    continue;
                }
            }

            try {
                hisopen = false;
                pb.directory(dir);
                pb.command(commandList.get(0));
                Process process = pb.start();
                InputStream is = process.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String line;
                while((line = br.readLine()) != null)
                    System.out.println(line);
                br.close();
            }
            catch(Exception e){
                System.out.println("Cannot run program " + (char)34 + commandLine + (char)34 + " (in directory " + (char)34 + System.getProperty("user.dir") + (char)34+"): " + "error=2, No such file or directory");
            }
        }
    }



    //////Additional helping function//////
    public static boolean isBlank(String s)
    {
        return (s == null) || (s.trim().length() == 0);
    }
    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch(NumberFormatException e) {
            return false;
        } catch(NullPointerException e) {
            return false;
        }
        return true;
    }
}

