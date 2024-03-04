package comp3111.covid;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class A3BubbleViewTest {
    A3BubbleView view;
    @Before
    public void setup() {
        view = new A3BubbleView(100, 100);
    }

    /**
     * test if the cosine thm is correctly implemented
     */
    @Test
    public void cosineThmTest() {
        double angle = A3BubbleView.cosineThm(10,10,10);
        assertEquals(angle, Math.PI / 3, 0.0001);
    }

    /**
     * overlapping circle test
     */
    @Test
    public void overlapTest() {
        ArrayList<A3BubbleView.CircleType> circles = new ArrayList<>();
        A3BubbleView.CircleType circle1 = new A3BubbleView.CircleType(10, "test");
        circle1.posY = 0;
        circle1.posX = 0;
        A3BubbleView.CircleType circle2 = new A3BubbleView.CircleType(10, "test2");
        circle2.posY = 0;
        circle2.posX = 0;
        circles.add(circle1);
        assertFalse(view.isCircleValid(circles, circle2, 0));
    }
    @Test
    public void notOverlapTest() {
        ArrayList<A3BubbleView.CircleType> circles = new ArrayList<>();
        A3BubbleView.CircleType circle1 = new A3BubbleView.CircleType(1, "test");
        circle1.posY = 0;
        circle1.posX = 0;
        A3BubbleView.CircleType circle2 = new A3BubbleView.CircleType(1, "test2");
        circle2.posY = 10;
        circle2.posX = 10;
        circles.add(circle1);
        assertTrue(view.isCircleValid(circles, circle2, 0));
    }

    /**
     * 90 degree test
     */
    @Test
    public void verticalAngle90Test() {
        A3BubbleView.CircleType circle1 = new A3BubbleView.CircleType(1, "test1");
        circle1.posX = 0;
        circle1.posY = 0;
        A3BubbleView.CircleType circle2 = new A3BubbleView.CircleType(0, "test2");
        circle2.posX = 1;
        circle2.posY = 0;
        double angle = A3BubbleView.getVerticalAngle(circle1, circle2);
        assertEquals(angle,Math.PI / 2, 0.001);
    }

    /**
     * 0 degree test
     */
    @Test
    public void verticalAngle0Test() {
        A3BubbleView.CircleType circle1 = new A3BubbleView.CircleType(1, "test1");
        A3BubbleView.CircleType circle2 = new A3BubbleView.CircleType(0, "test2");
        circle2.posX = 0;
        circle2.posY = 1;
        double angle = A3BubbleView.getVerticalAngle(circle1, circle2);
        assertEquals(angle,0, 0.001);
    }

    /**
     * -90 degree test
     */
    @Test
    public void verticalAngleN90Test() {
        A3BubbleView.CircleType circle1 = new A3BubbleView.CircleType(1, "test1");
        A3BubbleView.CircleType circle2 = new A3BubbleView.CircleType(0, "test2");
        circle2.posX = -1;
        circle2.posY = 0;
        double angle = A3BubbleView.getVerticalAngle(circle1, circle2);
        assertEquals(angle,-Math.PI / 2, 0.001);
    }

    /**
     * insert third circle test
     */
    @Test
    public void insertCircleTest() {
        A3BubbleView.CircleType circleO = new A3BubbleView.CircleType(1, "1");
        circleO.posX = 0;
        circleO.posY = 0;
        A3BubbleView.CircleType circleB = new A3BubbleView.CircleType(1, "2");
        circleB.posX = 2;
        circleB.posY = 0;
        ArrayList<A3BubbleView.CircleType> circles = new ArrayList<>();
        circles.add(circleO);
        circles.add(circleB);
        A3BubbleView.CircleType circleNew = new A3BubbleView.CircleType(1, "3");
        view.connectThirdCircle(circleO, circleB, circleNew);
        assertEquals(circleNew.posX, 1, 0.01);
        assertEquals(circleNew.posY, -2 * Math.sin(Math.PI / 3), 0.01);
    }

    /**
     * generate 2 circle test
     */
    @Test
    public void generate2CircleTest(){
        A3BubbleView.CircleType circle1 = new A3BubbleView.CircleType(1, "1");
        A3BubbleView.CircleType circle2 = new A3BubbleView.CircleType(1, "2");
        ArrayList<A3BubbleView.CircleType> circles = new ArrayList<>();
        circles.add(circle1);
        circles.add(circle2);
        view.generateBubbles(circles, 2);
        for(A3BubbleView.CircleType circle : circles){
            assertTrue(view.isCircleValid(circles, circle, 0.01));
        }
    }

    /**
     * Only one circle test
     */
    @Test
    public void generate1CircleTest(){
        A3BubbleView.CircleType circle1 = new A3BubbleView.CircleType(1, "1");
        ArrayList<A3BubbleView.CircleType> circles = new ArrayList<>();
        circles.add(circle1);
        view.generateBubbles(circles, 1);
        assertTrue(view.isCircleValid(circles, circle1, 0.01));
    }

    /**
     * Generate 10 circles test
     */
    @Test
    public void generate10CircleTest(){
        ArrayList<A3BubbleView.CircleType> circles = new ArrayList<>();
        for(int i = 0; i < 10; i++){
            circles.add(new A3BubbleView.CircleType(i, Integer.toString(i)));
        }
        view.generateBubbles(circles, 10);
        for(A3BubbleView.CircleType circle : circles){
            assertTrue(view.isCircleValid(circles, circle, 0.01));
        }
    }
}
