#Print out the letters that don't occur in a string

```
public class MissingLetters {
    public String getMissingLetters(String sentence){
        boolean[] letters = new boolean[26];
        for (int i = 0; i < letters.length; i ++){
            letters[i] = false;
        }
        for(int i = 0; i < sentence.length(); i++){
            char c = Character.toLowerCase(sentence.charAt(i));
            int index = c - 'a';
            if(index >= 0 && index < letters.length){
                letters[index] = true;
            }
        }
        String missingLetters = "";
        for (int i = 0; i < letters.length; i ++){
            if(letters[i] == false){
                char missingLetter = (char)(i + 'a');
                missingLetters += missingLetter;
            }
        }
        return missingLetters;
    }
    public static void main(String[] args){
        MissingLetters ms = new MissingLetters();
        String a =  "A slow yellow fox crawls under the proactive dog";
        String b = "Lions, and tigers, and bears, oh my!";
        String c =  "";
        System.out.println(ms.getMissingLetters(a));
        System.out.println(ms.getMissingLetters(b));
        System.out.println(ms.getMissingLetters(c));
    }
}
```