package base;

import java.util.Random;

public abstract class Entity {
    private int health;
    private int attack;

    public Entity(int health, int attack) {
        this.health = health;
        this.attack = attack;
    }

    public int getHealth() {
        return health;
    }

    public int getAttack() {
        return attack;
    }

    private void takeDamage(int amount) {
        health -= amount;
    }

    public boolean isAlive() {
        return health > 0;
    }

    public void modifyHealth(int min, int max){
        Random random = new Random();
        health += random.nextInt(max + 1 - min) + min;
    }

    public void modifyAttack(int min, int max){
        Random random = new Random();
        attack += random.nextInt(max + 1 - min) + min;;
    }

    public static void combat(Entity first, Entity second) {
        while(true){
            System.out.println("___________________________________________________________");

            System.out.printf("%s health = %d\n", first.getClass().getSimpleName(),first.getHealth());
            System.out.printf("%s health = %d\n\n", second.getClass().getSimpleName(),second.getHealth());

            System.out.printf("%s fa %d di danno a %s!\n",
                    first.getClass().getSimpleName(), first.getAttack(), second.getClass().getSimpleName());
            second.takeDamage(first.getAttack());
            if(!second.isAlive()){
                System.out.printf("%s è stato sconfitto!\n",second.getClass().getSimpleName());
                System.out.println("___________________________________________________________");
                return;
            }

            System.out.printf("%s fa %d di danno a %s!\n",
                    second.getClass().getSimpleName(), second.getAttack(), first.getClass().getSimpleName());
            first.takeDamage(second.getAttack());
            if(!first.isAlive()){
                System.out.printf("%s è stato sconfitto!\n",first.getClass().getSimpleName());
                System.out.println("___________________________________________________________");
                return;
            }
        }
    }
}
