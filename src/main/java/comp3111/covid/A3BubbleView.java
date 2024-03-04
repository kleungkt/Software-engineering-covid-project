package comp3111.covid;

import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.text.*;
import org.apache.commons.math.exception.ZeroException;

import java.util.*;

/**
 * Bubble view generator for task A3
 */
public class A3BubbleView {
    /**
     * Represent a circle in the view
     */
    static class CircleType {
        double radius;
        String name;
        double posX;
        double posY;
        Integer[] rgb;
        ArrayList<CircleType> neighbourList;

        /**
         * constructor of circle type
         * @param radius radius of the circle (will be scaled to match with the height)
         * @param name the name of this circle (country)
         */
        CircleType(double radius, String name) {
            this.radius = radius;
            this.name = name;
            rgb = new Integer[3];
            neighbourList = new ArrayList<>();
        }
    }

    HashMap<String, Integer[]> colorMap;
    Random random;
    double width;
    double height;

    /**
     * Constructor
     * @param width width of the pane
     * @param height height of the pane
     */
    A3BubbleView(double width, double height) {
        this.width = width;
        this.height = height;
        colorMap = new HashMap<>();
        random = new Random();
    }

    /**
     * check if the given circle is a valid circle, i.e. not overlapping
     * @param circles list of all circles
     * @param circleToTest circle to test
     * @param buffer some tolerance for overlapping
     * @return is the circle valid
     */
    public boolean isCircleValid(List<CircleType> circles, CircleType circleToTest, double buffer) {
        for (CircleType circleInList : circles) {
            if (circleToTest.name.equals(circleInList.name))
                continue;
            double minDistance = circleInList.radius + circleToTest.radius - buffer;
            double distX = circleInList.posX - circleToTest.posX;
            double distY = circleInList.posY - circleToTest.posY;
            double distance = Math.sqrt(distX * distX + distY * distY);
            if (distance <= minDistance)
                return false;
        }
        return true;
    }

    /**
     * Cosine Theorem
     * @param a Opposite side of target angle
     * @param b Adjacent side a
     * @param c Adjacent side b
     * @return Angle A
     */
    public static double cosineThm(double a, double b, double c) {
        return Math.acos((b * b + c * c - a * a) / (2 * b * c));
    }

    /**
     * get the angle between the given circles (vector) to the vertical line (downward)
     * @param circle1 circle 1 (center is origin of vector)
     * @param circle2 circle 2
     * @return Angle between vertical and the vector
     */
    public static double getVerticalAngle(CircleType circle1, CircleType circle2) {
        double px = circle2.posX - circle1.posX;
        double h = circle2.radius + circle1.radius;
        return Math.asin(px / h);
    }

    /**
     * initialize the top 2 circle (if the no. of cases > 1)
     * @param data list of circle
     */
    private void initTop2(List<CircleType> data) {
        data.get(0).posY = height / 2;
        data.get(0).posX = width / 4;
        data.get(1).posY = data.get(0).posY;
        data.get(1).posX = data.get(0).posX + data.get(0).radius + data.get(1).radius;
        data.get(0).neighbourList.add(data.get(1));
        data.get(1).neighbourList.add(data.get(0));
    }

    /**
     * place the third circle to be as close as the other 2
     * @param origin the first circle, served as origin
     * @param secondCircle the second circle
     * @param targetCircle the circle newly placed
     */
    public void connectThirdCircle(CircleType origin, CircleType secondCircle, CircleType targetCircle) {
        double vertAngle = getVerticalAngle(origin, secondCircle);
        double a = secondCircle.radius + targetCircle.radius;
        double b = origin.radius + secondCircle.radius;
        double c = origin.radius + targetCircle.radius;
        double angleA = cosineThm(a, b, c);
        double angleVertTarget = vertAngle + angleA;
        double targetX = origin.posX + Math.sin(angleVertTarget) * (origin.radius + targetCircle.radius);
        double targetY = origin.posY + Math.cos(angleVertTarget) * (origin.radius + targetCircle.radius);
        targetCircle.posX = targetX;
        targetCircle.posY = targetY;
    }
    public Integer[] getRGBColor(String name){
        Integer[] rgb;
        if (colorMap.containsKey(name)) {
            rgb = colorMap.get(name);
        } else {
            // assign random color to each circle
            rgb = new Integer[]{random.nextInt(256), random.nextInt(256), random.nextInt(256)};
            colorMap.put(name, rgb);
        }
        return rgb;
    }

    /**
     * Parse all the given circles and modify their position for generating the graph
     * @param data list of circle
     * @param topCountryNum the number of countries (circles), i.e. TOP X countries
     */
    public void generateBubbles(List<CircleType> data, int topCountryNum) {
        if(data.size() == 0)
            return;
        // sort in descending order
        data.sort((c1, c2) -> Double.compare(c2.radius, c1.radius));
        double scaleParam = height / 4 / data.get(0).radius;
        data.forEach(c -> c.radius *= scaleParam);
        if(data.size() == 1){
            data.get(0).posY = height / 2;
            data.get(0).posX = width / 4;
            data.get(0).rgb = getRGBColor(data.get(0).name);
            return;
        }
        initTop2(data);
        ArrayList<CircleType> assignedList = new ArrayList<>(data.subList(0, 2));

        for (int i = 2; i < topCountryNum; i++) {
            Collections.shuffle(assignedList);
            CircleType targetCircle = data.get(i);
            CircleType originCircle = assignedList.get(0);
            CircleType secondCircle = originCircle.neighbourList.get(random.nextInt(originCircle.neighbourList.size()));
            connectThirdCircle(originCircle, secondCircle, targetCircle);
            if (!isCircleValid(assignedList, targetCircle, 1)) {
                i--;
                continue;
            }
            originCircle.neighbourList.add(targetCircle);
            secondCircle.neighbourList.add(targetCircle);
            targetCircle.neighbourList.add(originCircle);
            targetCircle.neighbourList.add(secondCircle);
            assignedList.add(targetCircle);

        }

        for (CircleType circle : data) {
            circle.rgb = getRGBColor(circle.name);
        }
    }
}
