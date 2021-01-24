package com.geekbrains;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Phaser;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class MainClass {
    public static final int CARS_COUNT = 4;
    public static void main(String[] args) {
        System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Подготовка!!!");
        Race race = new Race(new Road(60), new Tunnel(), new Road(40));

        Phaser phaser = new Phaser(CARS_COUNT);

        Car[] cars = new Car[CARS_COUNT];
        for (int i = 0; i < cars.length; i++) {
            cars[i] = new Car(race, 20 + (int) (Math.random() * 10),phaser);
            new Thread(cars[i]).start();
        }
        phaser.awaitAdvance(0);
        System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка началась!!!");
        phaser.awaitAdvance(1);
        System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка закончилась!!!");
    }
}
class Car implements Runnable {

    private static volatile AtomicInteger racePosition = new AtomicInteger();
    private static int CARS_COUNT;

    private Race race;
    private int speed;
    private String name;
    private Phaser phaser;

    public String getName() {
        return name;
    }
    public int getSpeed() {
        return speed;
    }
    public Car(Race race, int speed, Phaser phaser) {
        this.race = race;
        this.speed = speed;
        this.phaser = phaser;

        CARS_COUNT++;
        this.name = "Участник #" + CARS_COUNT;
    }
    @Override
    public void run() {
        try {
            System.out.println(this.name + " готовится");
            Thread.sleep(500 + (int)(Math.random() * 800));
            System.out.println(this.name + " готов");

            phaser.arriveAndAwaitAdvance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < race.getStages().size(); i++) {
            race.getStages().get(i).go(this);
        }

        System.err.println(this.name + " занял " + racePosition.incrementAndGet() + " место");
        phaser.arrive();
    }
}
abstract class Stage {
    protected int length;
    protected String description;

    public String getDescription() {
        return description;
    }
    public abstract void go(Car c);
}
class Road extends Stage {
    public Road(int length) {
        this.length = length;
        this.description = "Дорога " + length + " метров";
    }
    @Override
    public void go(Car c) {
        try {
            System.out.println(c.getName() + " начал этап: " + description);
            Thread.sleep(length / c.getSpeed() * 1000L);
            System.out.println(c.getName() + " закончил этап: " + description);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
class Tunnel extends Stage {

    private final Semaphore semaphore;

    public Tunnel() {
        this.length = 80;
        this.description = "Тоннель " + length + " метров";
        semaphore = new Semaphore(MainClass.CARS_COUNT/2);
    }
    @Override
    public void go(Car c) {
        try {
            try {
                if(!semaphore.tryAcquire()) {
                    System.err.println(c.getName() + " готовится к этапу(ждет): " + description);
                    semaphore.acquire();
                }

                System.err.println(c.getName() + " начал этап: " + description);
                Thread.sleep(length/c.getSpeed() * 1000L);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                System.err.println(c.getName() + " закончил этап " + description);
                semaphore.release();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
class Race {
    private ArrayList<Stage> stages;
    public ArrayList<Stage> getStages() { return stages; }
    public Race(Stage... stages) {
        this.stages = new ArrayList<>(Arrays.asList(stages));
    }
}
