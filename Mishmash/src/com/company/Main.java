package com.company;

import java.util.*;
import java.util.List;
import java.util.regex.Pattern;

//2019203102 유지성

// 단어 우월 효과를 이용한 말장난
// 입력받은 단어의 글자 순서를 바꿔주는 프로그램
// 한국어 단어와 영어 단어의 순서만 바꿈
// "안녕하세요" -> "안세하녕요", "hello" -> "hlleo"
// "he안녕l", "hi321"과 같은 단어는 바꾸지 않음
// 한국어일 경우 최대 6자
// 영어일 경우 최대 10자
// 처음과 끝 2글자를 남기고 글자 순서를 바꿈

// 한글과 영어 말고 다른 언어 변환하는법
// 1. 새로운 MishMash class를 만들음
// 2. CheckLang에 다른 언어를 확인하는 방법을 추가

public class Main {

    public static void main(String[] args) {
        System.out.println("단어 우월 효과");
        System.out.println("\"캠릿브지 대학의 연결구과에 따르면...\"");
        System.out.println("=======================================");
        System.out.println("길이가 너무 긴 단어는 효과가 떨어지므로");
        System.out.println("한글 : 6글자, 영어 : 8글자까지만 교체됩니다.");
        System.out.println("(각 단어는 띄어쓰기로 구분됨)");
        System.out.println("\n문장을 입력하십시오.");
        System.out.print(">> ");
        Scanner sc = new Scanner(System.in);
        String sin = sc.nextLine();

        String[] strArr = sin.split(" ");
        List<String> strList = Arrays.asList(strArr);


        //람다식을 이용한 한글, 영문 구분
        //한글인지 체크
        CheckLang isKor = (word) -> {
            boolean kor = true;
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < word.length(); i++) {
                char temp = word.charAt(i);
                String index = Character.toString(temp);

                //끝에 특수문자인지 체크
                if (i == word.length() - 1) {
                    if (Pattern.matches("[!@#$%^&*(),.?\'\":{}|<>]", index)) {
                        continue;
                    }
                }

                if (!Pattern.matches("^[가-힣]*$", index)) {
                    return false;
                }
            }
            return kor;
        };

        //영어인지 체크
        CheckLang isEng = (word) -> {
            boolean alpha = true;
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < word.length(); i++) {
                int index = word.charAt(i);
                String tIndex = Character.toString(word.charAt(i));

                //끝에 특수문자인지 체크
                if (i == word.length() - 1) {
                    if (Pattern.matches("[!@#$%^&*(),.?\'\":{}|<>]", tIndex)) {
                        continue;
                    }
                }

                //영어는 단어 중간에 '나와도 okay 39번 ' 추가
                if (!(index >= 65 && index <= 122 || index == 39)) {
                    alpha = false;
                }
            }
            return alpha;
        };

        ArrayList<MishBox<MishMash>> list = new ArrayList<>();
        Iterator<String> strIt = strList.listIterator();

        while (strIt.hasNext()) {
            String s = strIt.next();
            MishBox<MishMash> ms = new MishBox<>();

            if (isKor.check(s)) {
                ms.set(new MishMashKor(s));
            } else if (isEng.check(s)) {
                ms.set(new MishMashEng(s));
            } else
                ms.set(new MishMashElse(s));

            list.add(ms);
        }

        Iterator<MishBox<MishMash>> itr = list.iterator();
        System.out.print("결과 : ");
        while (itr.hasNext()) {
            MishBox<MishMash> m = itr.next();
            System.out.print(m + " ");
        }
    }
}

// 단어가 어떤 언어로 이루어져있는지 체크
interface CheckLang {
    boolean check(String s);
}

// MishMash 클래스들을 위한 interface
interface MishMash {
    String mix(String s);   // 단어의 글자 순서를 바꿈. 바뀐 단어를 return
    String getStr();        // 순서를 바꾼 단어를 return
    void setStr(String s);  // 순서를 바꿀 단어를 설정
}

// MishMash 클래스들을 담는 박스
class MishBox<T extends MishMash> {
    T mishmash;

    public void set(T ms) {
        mishmash = ms;
    }

    @Override
    public String toString() {
        return mishmash.getStr();
    }
}

// 한국어 MishMash
class MishMashKor implements MishMash {
    String str;

    MishMashKor() {

    }

    MishMashKor(String s) {
        str = mix(s);
    }

    @Override
    public String mix(String s) {
        int maxLength = 6;
        int changeIndex = s.length() - 1; //글자를 바꿀때 필요한 index값
        boolean checkSpecial = false;

        //마지막 글자가 특수문자일때 그 문자를 제외함
        String index = Character.toString(s.charAt(changeIndex));
        if (Pattern.matches("[!@#$%^&*(),.?\'\":{}|<>]", index)) {
            changeIndex--;
            checkSpecial = true;
            maxLength++;
        }

        //글자가 6글자 넘어갈 경우 실행 안함
        if (s.length() > maxLength)
            return s;

        StringBuffer string = new StringBuffer();
        int maxIndex = changeIndex + 1;

        string.append(s.charAt(0));
        if (s.length() > 1) { //단어가 하나의 글자로만 이루어져 있을경우 실행 안함
            // 글자의 순서를 바꿔서 StringBuffer에 append
            for (int i = 1; i < changeIndex; i++) {
                string.append(s.charAt(maxIndex - 1 - i));
            }
            string.append(s.charAt(maxIndex - 1));
        }

        if (checkSpecial)   // 끝 글자가 특수문자일때
            string.append(s.charAt(maxIndex));

        return string.toString();
    }

    @Override
    public String getStr() {
        return str;
    }

    @Override
    public void setStr(String s) {
        str = mix(s);
    }

    @Override
    public String toString() {
        return str;
    }
}

// 영어 MishMash
class MishMashEng implements MishMash {
    String str;

    MishMashEng() {

    }

    MishMashEng(String s) {
        str = mix(s);
    }

    @Override
    public String mix(String s) {
        int maxLength = 8;
        int changeIndex = s.length() - 1; //글자를 바꿀때 필요한 index값
        boolean checkSpecial = false;

        //마지막 글자가 특수문자일때 그 문자를 제외함
        String index = Character.toString(s.charAt(changeIndex));
        if (Pattern.matches("[!@#$%^&*(),.?\'\":{}|<>]", index)) {
            changeIndex--;
            checkSpecial = true;
            maxLength++;
        }

        //글자가 8글자 넘어갈 경우 실행 안함
        if (s.length() > maxLength)
            return s;

        StringBuffer string = new StringBuffer();
        int maxIndex = changeIndex + 1;

        int checkApost = -1; // apostrophe가 있는지 확인하는 변수

        string.append(s.charAt(0));
        if (s.length() > 1) {   // 단어가 하나의 글자로만 이루어져 있을경우 실행 안함
            // 글자의 순서를 바꿔서 StringBuffer에 append
            for (int i = 1; i < changeIndex; i++) {
                if (s.charAt(i) == '\'')    // apostrophe가 단어에 존재할때 실행
                    checkApost = i;

                string.append(s.charAt(maxIndex - 1 - i));
            }
            string.append(s.charAt(maxIndex - 1));
        }

        if (checkApost != -1) {  // apostrophe가 있으면 apostrophe를 원래 위치로 보냄
            string.insert(checkApost + 1, '\'');
            string.deleteCharAt(maxIndex - 1 - checkApost);
        }

        if (checkSpecial)   // 끝 글자가 특수문자일때
            string.append(s.charAt(maxIndex));

        return string.toString();
    }

    @Override
    public String getStr() {
        return str;
    }

    @Override
    public void setStr(String s) {
        str = mix(s);
    }

    @Override
    public String toString() {
        return str;
    }
}

// 정의되지 않은 MishMash
// 입력된 글자는 변환이 이루어지지 않음
class MishMashElse implements MishMash {
    String str;

    MishMashElse() {

    }

    MishMashElse(String s) {
        str = s;
    }

    @Override
    public String mix(String s) {
        return s;
    }

    @Override
    public String getStr() {
        return str;
    }

    @Override
    public void setStr(String s) {
        str = s;
    }

    @Override
    public String toString(){
        return str;
    }
}
