package org.example;


import java.util.List;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
//        if (args.length == 2) {
//            HandlerClass test1 = new HandlerClass(args[0], args[1]);
//            try {
//                List<String> result = test1.handler();
//                System.out.println(result);
//            } catch (Exception e) {
//                String error = e.toString();
//                System.out.println(error);
//            }
//        } else {
//            System.out.println("Invalid input parameters in command line arguments");
//        }



        HandlerClass test1 = new HandlerClass("stat", "/Users/wsulu/Desktop/aikamsoft/aikamsoft/src/main/java/org/example/stat.json");
        try {
            List<String> result = test1.handler();
            System.out.println(result);
        } catch (Exception e) {
            String error = e.toString();
            System.out.println(error);
        }

    }
}
