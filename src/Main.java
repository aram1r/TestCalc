import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class Main {
    public static void main(String[] args) {
        BufferedReader br = null;
        String input = "";
        try {
            br = new BufferedReader(new InputStreamReader(System.in));
            while(true) {
                System.out.println("Введите пример для решения");
                input = br.readLine();
                LexemeBuffer lexemeBuffer = new LexemeBuffer(input);
                System.out.println(Calculator.getResult(lexemeBuffer));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
