package io.lazyii.captcha;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import java.util.stream.IntStream;


/**
 * 所有坐标，右移n：x+n 下移n: y+n
 */
public class RandomCircles {
    
    private Bounds DEFAULT_BOUNDS = new Bounds(0, 0, 400f, 200f);
    
    float r = 30;
    //画布边界
    Bounds bounds = DEFAULT_BOUNDS;
    List<Circle> circles = new ArrayList<>();
    
    
    /**
     * 判断已经存在的圆是否与 此圆相交
     * @return false:未相交 true相交(边界重合也算相交)
     */
    public boolean intersect(Circle cr) {
        if (circles.isEmpty()) {
            return false;
        } else {
            //已存在圆的圆心坐标与此圆 x轴，y轴 差值都大于r的，肯定不会相交。
            return circles
                    .stream()
                    .map(x -> new Tuple<Float, Float>(Math.abs(x.getX() - cr.getX()), Math.abs(x.getY() - cr.getY())))
                    .filter(x -> x._1 <= r || x._2 <= r)
                    .anyMatch(x -> 4 * r * r >= x._1 * x._1 + x._2 * x._2);
        }
    }
    
    public List<Circle> randomCrs(int num) {
        return randomCrs(num, 4 * num);
    }
    
    public List<Circle> randomCrs(int num, int tryTimes) {
        if (bounds == null) {
            throw new RuntimeException("bounds is null, please init bounds first");
        } else if (num > tryTimes) {
            throw new RuntimeException("error! tryTimes must larger than num");
        } else {
            IntStream
                    .rangeClosed(0, tryTimes)
                    .mapToObj(x -> randomCr())
                    .filter(Predicate.not(this::intersect))
                    .peek(circles::add)
                    .limit(num)
                    .count();
        }
        return circles;
    }
    
    public Circle randomCr() {
        float x1 = bounds.x + bounds.width - r;
        float y1 = bounds.y + bounds.height - r;
        float x2 = (float) ThreadLocalRandom.current().doubles(1, r, x1).findAny().getAsDouble();
        float y2 = (float) ThreadLocalRandom.current().doubles(1, r, y1).findAny().getAsDouble();
        return new Circle(x2, y2, r);
    }
    
    class Bounds{
        float x;
        float y;
        float width;
        float height;
    
        public Bounds() {}
        
        public Bounds(float x, float y, float width, float height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }
    
    public static void main(String[] args) {
        RandomCircles randomCircles = new RandomCircles();
        List<Circle> list = randomCircles.randomCrs(2);
        System.out.println(list);
    }
}
