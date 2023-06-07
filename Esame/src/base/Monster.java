package base;

public class Monster extends Entity {
    public Monster() {
        super(12,3);
        modifyHealth(-5,5);
        modifyAttack(-2,2);
    }
}
