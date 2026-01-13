package dmit2015.console;

import ca.nait.dmit.Main;

public class Circle {

    // Define field to track the radius of the Circle
    private double radius;

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        if (radius < 0) {
            throw new IllegalArgumentException("Radius cannot be negative");
        }
        this.radius = radius;
    }

    public Circle() {
        setRadius(1.0);
    }

    public Circle(double radius) {
//        this.radius = radius;
        setRadius(radius);
    }

    public double getDiameter() {
        return radius * 2;
    }

    public double getArea() {
        return Math.PI * radius * radius;
    }

    public double getPerimeter() {
        return 2 * Math.PI * radius;
    }
}
