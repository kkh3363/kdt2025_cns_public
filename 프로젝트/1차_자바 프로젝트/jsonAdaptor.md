
```
dependencies {
    implementation("org.json:json:20240303")
}
```

```
public class PersonDto {
    String name;
    int age;

    public PersonDto(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
```

```
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

public class test1 {
    public static  void main(String[] args) {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<PersonDto> personAdapter = moshi.adapter(PersonDto.class);
        // JSON -> 객체
        String json = "{ \"name\": \"John Doe\", \"age\": 30 }";
        try {
            PersonDto person = personAdapter.fromJson(json);
            System.out.println("Name: " + person.name + ", Age: " + person.age); // 출력: Name: John Doe, Age: 30
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 객체 -> JSON
        PersonDto newPerson = new PersonDto("Jane Doe", 25);
        String newJson = personAdapter.toJson(newPerson);
        System.out.println(newJson); // 출력: {"name":"Jane Doe","age":25}
    }
```
