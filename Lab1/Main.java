class NotValidArguments extends Exception{
    public NotValidArguments(String errorMessage){
        super(errorMessage);
    }
}


public class Main {
    public static void main(String[] args) throws NotValidArguments{
        if(args.length != 1){
            throw new NotValidArguments("WrongCountOfArguments");
        } else{
            Config config = new Config(args[0]);
        }

    }
}
