package com.byd.dynaudio_app.utils;


/**
 */

public class MyPoint2D {
    private final Double x;

    private final Double y;

    public MyPoint2D(Double x, Double y) {
        this.x = x;
        this.y = y;
    }

  public   static class MyTriangle2D {
        private MyPoint2D p1;
        private MyPoint2D p2;
        private MyPoint2D p3;

        public MyTriangle2D() {
            this.p1 = new MyPoint2D(0.0, 0.0);
            this.p2 = new MyPoint2D(1.0, 1.0);
            this.p3 = new MyPoint2D(2.0, 5.0);
        }

        public MyTriangle2D(MyPoint2D p1, MyPoint2D p2, MyPoint2D p3) {
            this.p1 = p1;
            this.p2 = p2;
            this.p3 = p3;
        }

//        利用海伦公式
        public double getArea() {
            double p = getPerimeter() * 0.5;
            return Math.sqrt(p*(p - getSide1Len())*(p - getSide2Len())*(p - getSide3Len()));
        }

        /**
         * @return p1, p2 点之间的线段长度
         */
        private double getSide1Len() {
            return Math.sqrt(Math.pow((p1.x - p2.x), 2) + Math.pow((p1.y - p2.y), 2));
        }

        /**
         * @return p1, p3 点之间的线段长度
         */
        private double getSide2Len() {
            return Math.sqrt(Math.pow((p1.x - p3.x), 2) + Math.pow((p1.y - p3.y), 2));
        }

        /**
         * @return p2, p3 点之间的线段长度
         */
        private double getSide3Len() {
            return Math.sqrt(Math.pow((p2.x - p3.x), 2) + Math.pow((p2.y - p3.y), 2));
        }

        public double getPerimeter() {
            double sideLength1 = getSide1Len();
            double sideLength2 = getSide2Len();
            double sideLength3 = getSide3Len();
            return sideLength1 + sideLength2 + sideLength3;
        }

        /**
         * 重心法判断点是否在三角形内部, v0，v1和v2是向量
         * v0 = C – A, v1 = B – A, v2 = P – A，则v2 = u * v0 + v * v1
         * 当 u == 1 且 v == 0 时, 则在 C点上, u == 0 且 v == 0 则在 B点上
         * @param p 二维点
         * @return 点在三角形内部的真值
         */
        public boolean contains(MyPoint2D p) {
            Vector v0 = new Vector(p3.x - p1.x, p3.y - p1.y);
            Vector v1 = new Vector(p2.x - p1.x, p2.y - p1.y);
            Vector v2 = new Vector(p.x - p1.x, p.y - p1.y);
            double dot00 = dot(v0, v0);
            double dot01 = dot(v0, v1);
            double dot02 = dot(v0, v2);
            double dot11 = dot(v1, v1);
            double dot12 = dot(v1, v2);
            double invDenom = 1 / (dot00 * dot11 - dot01 * dot01);
            double u = (dot11 * dot02 - dot01 * dot12) * invDenom;
            double v = (dot00 * dot12 - dot01 * dot02) * invDenom;
            if ((0.0 == u && 1.0 == v) || (0.0 == v && 1.0 == u)) {
                return true;
            }
            return (u >= 0) && (v >= 0) && (u + v < 1);
        }

        private double dot(Vector v1, Vector v2) {
            return (v1.x * v2.x) + (v1.y * v2.y);
        }

        public boolean contains(MyTriangle2D triangle2D) {
            return contains(triangle2D.p1) && contains(triangle2D.p2) && contains(triangle2D.p3);
        }
    }

    static class Line{

        MyPoint2D p1;

        MyPoint2D p2;


        public Line(MyPoint2D p1, MyPoint2D p2) {
            this.p1 = p1;
            this.p2 = p2;
        }

        boolean contains(MyPoint2D point) {
            return 0 == multiply(point, p2, p2);
        }

        public static double multiply(MyPoint2D target, MyPoint2D p1, MyPoint2D p2) {
            return ((p1.x - target.x)*(p2.y - target.y) - (p2.x - target.x)*(p1.y - target.y));
        }
    }


    static class Vector{
        double x;
        double y;

        public Vector(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }
}

