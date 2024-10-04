package ru.otus.otuskotlin.oop

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals

class Hw1KtTest {
    // task 1 - make a Rectangle class that will have width and height
    // as well as the area calculation method - area()
    // the test below should pass - uncomment the code in it

    class Rectangle (val width: Int, val height: Int): Figure {
        override fun area(): Int {
            return this.width * this.height
        }

        override fun toString(): String {
            return "Rectangle(${this.width}x${this.height})"
        }

        override fun equals(other: Any?): Boolean {
            if (other is Rectangle) {
                return this.width == other.width && this.height == other.height
            }
            return false
        }

        override fun hashCode(): Int {
            return this.width*this.height
        }
    }


    @Test
    fun rectangleArea() {
        val r = Rectangle(10, 20)
        assertEquals(200, r.area())
        assertEquals(10, r.width)
        assertEquals(20, r.height)
    }

    // task 2 - make the Rectangle.toString() method
    // the test below should pass - uncomment the code in it
    @Test
    fun rectangleToString() {
        val r = Rectangle(10, 20)
        assertEquals("Rectangle(10x20)", r.toString())
    }

    // task 3 - make Rectangle.equals() and Rectangle.hashCode() methods
    // the test below should pass - uncomment the code in it
    @Test
    fun rectangleEquals() {
        val a = Rectangle(10, 20)
        val b = Rectangle(10, 20)
        val c = Rectangle(20, 10)
        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
        assertFalse (a === b)
        assertNotEquals(a, c)
    }

    class Square (val width: Int): Figure {
        override fun area() : Int {
            return this.width*this.width
        }

        override fun equals(other: Any?): Boolean {
            return if (other is Square) {
                this.width == other.width
            } else false
        }

        override fun hashCode(): Int {
            return this.width
        }
    }

    // task 4 - make the Square class
    // the test below should pass - uncomment the code in it
    @Test
    fun squareEquals() {
        val a = Square(10)
        val b = Square(10)
        val c = Square(20)
        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
        assertFalse (a === b)
        assertNotEquals(a, c)
        println(a)
    }

    interface Figure {
        fun area():Int
    }

    // task 5 - make the Figure interface with the area() method, inherit Rectangle and Square from it
    // the test below should pass - uncomment the code in it
    @Test
    fun figureArea() {
        var f : Figure = Rectangle(10, 20)
        assertEquals(200, f.area())

        f = Square(10)
        assertEquals(100, f.area() )
    }

    fun getDiffArea (figure1: Figure, figure2: Figure): Int {
        return figure1.area()-figure2.area()
    }

    // task 6 - make the diffArea(a, b) method
    // the test below should pass - uncomment the code in it
    @Test
    fun diffArea() {
        val a = Rectangle(10, 20)
        val b = Square(10)
        assertEquals(100, getDiffArea(a, b))
    }
}
