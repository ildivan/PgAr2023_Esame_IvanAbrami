import base.BaseModule;
import it.kibo.fp.lib.Menu;
import module11.Module11;
import module2.Module2;
import module3.Module3;
import module42.Module42;

public class Main {
    public static void main(String[] args) {

        String moduleMenuTitle = "Scegli modulo da eseguire";
        String[] moduleMenuEntries = {
                "Modulo base",
                "Modulo 1.1",
                "Modulo 2",
                "Modulo 3",
                "Modulo 4.2"
        };
        Menu moduleMenu = new Menu(moduleMenuTitle, moduleMenuEntries, true, true, true);

        try{
            switch(moduleMenu.choose()){
                case 1 -> {
                    BaseModule.start();
                }
                case 2 -> {
                    Module11.start();
                }
                case 3 -> {
                    Module2.start();
                }
                case 4 -> {
                    Module3.start();
                }
                case 5 -> {
                    Module42.start();
                }
            }
        }catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }


}