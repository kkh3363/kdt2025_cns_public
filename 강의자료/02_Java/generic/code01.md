### Beverage.java
```
public class Beverage {
}
```
### Boricha.java
```
public class Boricha extends Beverage {
}
```
### Cup.java
```
public class Cup<T extends Beverage> {

}
```
### Beer.java
```
public class Beer extends Beverage {
}
```
### object/Cup.java
```
public class Cup {
    private Object beverage;

    public Object getBeverage() {
        return beverage;
    }

    public void setBeverage(Object beverage) {
        this.beverage = beverage;
    }
}
### GenericClass1Demo.java
```
import sec03.object.Cup;

public class GenericClass1Demo {
    public static void main(String[] args) {
        Cup c = new Cup();

        c.setBeverage(new Beer());
        Beer b1 = (Beer) c.getBeverage();

        c.setBeverage(new Boricha());
        // b1 = (Beer) c.getBeverage();
    }
}
```
