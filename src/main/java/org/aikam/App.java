package org.aikam;

public class App 
{
    public static void main( String[] args )
    {
        String error = null;
        if (args.length == 2) {
            HandlerClass test1 = new HandlerClass(args[0], args[1]);
            error = test1.handler();
        } else {
            error = "Invalid input parameters in command line arguments";
        }
        if (error != null) {
            ParserJson er = new ParserJson("err.json");
            er.writerError(error);
        }
    }
}
