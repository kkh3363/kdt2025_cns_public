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
