### StringTokennizer 란?

- 구분자로 문자열을 나누어주는 클래스입니다.
- 더 이상 나눌 수 없는 요소들을 Token이라고 합니다.
- 전화번호로 예를 들면, 010-1234-5678 이라는 번호에서
- - 는 구분자를 뜻 하는 것이고 010, 1234, 5678 은 Token(토큰) 인 것입니다.

### 생성자(Constructor)
![생성자_01](https://github.com/user-attachments/assets/589bf3b2-f38d-4b94-a589-eee5d339b42c)




### method
![매소드_01](https://github.com/user-attachments/assets/1ee7752f-b1d0-4d37-997e-885bfc4d59dd)


### 예제 코드
```
public static void main(String[] args) {
        String str = "hello \n my \f name \t is \r pro nine";
        System.out.println("str에 담긴 값은 : " + str);
        StringTokenizer tokenizer = new StringTokenizer(str);

        System.out.println("=== String Tokenizer Strart ===");
        while (tokenizer.hasMoreTokens()){
            System.out.println(tokenizer.nextToken());
        }
    }
![image](https://github.com/user-attachments/assets/114180cc-1f28-46f7-971e-4c5cb423729d)

```
