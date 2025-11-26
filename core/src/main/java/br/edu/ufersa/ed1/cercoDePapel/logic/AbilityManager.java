package br.edu.ufersa.ed1.cercoDePapel.logic;

import br.edu.ufersa.ed1.cercoDePapel.entities.BoardUnit;

import java.util.List;
import java.util.Random;

public class AbilityManager {
    private static final Random random = new Random();

    public static boolean executeAbility(BoardUnit unit, BattleManager battleManager, int targetX, int targetY) {
        return switch (unit.getCardData().name) {
            case "Clérigo" -> executeHeal(unit, battleManager, targetX, targetY);
            case "Assassino" -> executeBackstab(unit, battleManager);
            case "Arquimago" -> executeLineAttack(unit, battleManager);
            case "Paladino" -> executeProtection(unit, battleManager);
            case "Esqueleto" -> executeRevive(unit, battleManager);
            case "Ogro" -> executeSplashDamage(unit, battleManager);
            case "Golem de Ferro" -> executeDamageReduction(unit, battleManager);
            case "Dragão Vermelho" -> executeDragonAttack(unit, battleManager);
            case "Rato Gigante" -> executePoison(unit, battleManager);
            case "Slime" -> executeDamageReflection(unit, battleManager);
            default -> false;
        };
    }

    private static boolean executeHeal(BoardUnit cleric, BattleManager battleManager, int targetX, int targetY) {
        // Clérigo cura uma unidade aliada específica no alvo selecionado
        BoardUnit target = battleManager.getUnitAt(targetX, targetY);

        if (target == null) {
            System.out.println("Nenhuma unidade no alvo selecionado!");
            return false;
        }

        // Verifica se o alvo é aliado
        if (target.isPlayer() != cleric.isPlayer()) {
            System.out.println("Só pode curar unidades aliadas!");
            return false;
        }

        // Verifica se o alvo não é o próprio clérigo
        if (target == cleric) {
            System.out.println("Não pode curar a si mesmo!");
            return false;
        }

        // Verifica alcance (alcance de 2 para cura)
        int distance = Math.abs(cleric.getGridX() - targetX) +
            Math.abs(cleric.getGridY() - targetY);

        if (distance > 2) {
            System.out.println("Alvo fora do alcance de cura!");
            return false;
        }

        // Verifica se o alvo precisa de cura
        if (target.getCurrentHp() >= target.getCardData().hp) {
            System.out.println("O alvo já está com a vida cheia!");
            return false;
        }

        int healAmount = 2;
        int newHp = Math.min(target.getCurrentHp() + healAmount, target.getCardData().hp);
        int actualHeal = newHp - target.getCurrentHp();
        target.takeDamage(-actualHeal);
        System.out.println("Clérigo curou " + target.getCardData().name + " em " + actualHeal + " de vida!");
        return true;
    }

    private static boolean executeBackstab(BoardUnit assassin, BattleManager battleManager) {
        // Assassino: verifica se pode fazer ataque pelas costas no próximo ataque
        List<BoardUnit> enemies = battleManager.getUnits().stream()
            .filter(u -> u.isPlayer() != assassin.isPlayer())
            .toList();

        for (BoardUnit enemy : enemies) {
            if (isBackstabPosition(assassin, enemy)) {
                assassin.addEffect("backstab_ready", 1);
                System.out.println("Assassino preparou ataque pelas costas!");
                return true;
            }
        }
        return false;
    }

    private static boolean isBackstabPosition(BoardUnit assassin, BoardUnit target) {
        int dx = target.getGridX() - assassin.getGridX();
        int dy = target.getGridY() - assassin.getGridY();

        return (target.isPlayer() && dx > 0) || (!target.isPlayer() && dx < 0);
    }

    private static boolean executeLineAttack(BoardUnit archmage, BattleManager battleManager) {
        // Arquimago: ataque em linha que perfura múltiplos inimigos
        List<BoardUnit> enemies = battleManager.getUnits().stream()
            .filter(u -> u.isPlayer() != archmage.isPlayer())
            .toList();

        boolean attacked = false;
        for (BoardUnit enemy : enemies) {
            if (isInLine(archmage, enemy) && isInRange(archmage, enemy)) {
                int damage = calculateDamage(archmage);
                enemy.takeDamage(damage);
                System.out.println("Raio do Arquimago causou " + damage + " de dano em " + enemy.getCardData().name);

                if (enemy.isDead()) {
                    battleManager.getUnits().remove(enemy);
                }
                attacked = true;
            }
        }
        return attacked;
    }

    private static boolean isInLine(BoardUnit source, BoardUnit target) {
        return source.getGridX() == target.getGridX() || source.getGridY() == target.getGridY();
    }

    private static boolean executeProtection(BoardUnit paladin, BattleManager battleManager) {
        // Paladino: protege aliados adjacentes
        List<BoardUnit> allies = battleManager.getUnits().stream()
            .filter(u -> u.isPlayer() == paladin.isPlayer() && u != paladin)
            .toList();

        boolean protectedSomeone = false;
        for (BoardUnit ally : allies) {
            int distance = Math.abs(ally.getGridX() - paladin.getGridX()) +
                Math.abs(ally.getGridY() - paladin.getGridY());

            if (distance == 1) { // Adjacente
                ally.addEffect("protected_by_paladin", 1);
                System.out.println("Paladino está protegendo " + ally.getCardData().name);
                protectedSomeone = true;
            }
        }
        return protectedSomeone;
    }

    private static boolean executeRevive(BoardUnit skeleton, BattleManager battleManager) {
        return false;
    }

    private static boolean executeSplashDamage(BoardUnit ogre, BattleManager battleManager) {
        ogre.addEffect("splash_damage", 1);
        System.out.println("Ogro preparou ataque com dano em área!");
        return true;
    }

    private static boolean executeDamageReduction(BoardUnit golem, BattleManager battleManager) {
        return false;
    }

    private static boolean executeDragonAttack(BoardUnit dragon, BattleManager battleManager) {
        if (!dragon.canUseAbility()) {
            System.out.println("Dragão Vermelho está em cooldown!");
            return false;
        }

        List<BoardUnit> enemies = battleManager.getUnits().stream()
            .filter(u -> u.isPlayer() != dragon.isPlayer())
            .toList();

        boolean attacked = false;
        for (BoardUnit enemy : enemies) {
            if (isInRange(dragon, enemy)) {
                int damage = calculateDamage(dragon);

                enemy.takeDamage(damage);
                System.out.println("Dragão atacou " + enemy.getCardData().name + " causando " + damage + " de dano!");

                applyAreaDamage(dragon, enemy, damage / 2, battleManager);

                dragon.setCooldown(1);
                attacked = true;
                break;
            }
        }
        return attacked;
    }

    private static boolean executePoison(BoardUnit rat, BattleManager battleManager) {
        rat.addEffect("poison_attack", 1);
        System.out.println("Rato Gigante preparou ataque venenoso!");
        return true;
    }

    private static boolean executeDamageReflection(BoardUnit slime, BattleManager battleManager) {
        return false;
    }

    // Métodos auxiliares
    private static boolean isInRange(BoardUnit source, BoardUnit target) {
        int distance = Math.abs(source.getGridX() - target.getGridX()) +
            Math.abs(source.getGridY() - target.getGridY());
        return distance <= source.getCardData().range;
    }

    private static int calculateDamage(BoardUnit unit) {
        int min = unit.getCardData().dmgMin;
        int max = unit.getCardData().dmgMax;
        return random.nextInt((max - min) + 1) + min;
    }

    private static void applyAreaDamage(BoardUnit source, BoardUnit mainTarget, int areaDamage, BattleManager battleManager) {
        for (BoardUnit unit : battleManager.getUnits()) {
            if (unit != mainTarget && unit.isPlayer() != source.isPlayer()) {
                int distance = Math.abs(unit.getGridX() - mainTarget.getGridX()) +
                    Math.abs(unit.getGridY() - mainTarget.getGridY());
                if (distance == 1) {
                    unit.takeDamage(areaDamage);
                    System.out.println("Dano em área: " + areaDamage + " de dano em " + unit.getCardData().name);
                }
            }
        }
    }

    // Método para aplicar efeitos durante o ataque
    public static int applyAttackEffects(BoardUnit attacker, BoardUnit target, int baseDamage) {
        int finalDamage = baseDamage;

        if (attacker.hasEffect("backstab_ready") && isBackstabPosition(attacker, target)) {
            finalDamage += 2;
            System.out.println("Ataque pelas costas! +2 de dano");
            attacker.removeEffect("backstab_ready");
        }

        if (attacker.hasEffect("splash_damage")) {
            BattleManager battleManager = getBattleManagerInstance();
            if (battleManager != null) {
                applyAreaDamage(attacker, target, baseDamage / 2, battleManager);
            }
            attacker.removeEffect("splash_damage");
        }

        if (attacker.hasEffect("poison_attack")) {
            target.addEffect("poisoned", 3);
            System.out.println(target.getCardData().name + " foi envenenado!");
            attacker.removeEffect("poison_attack");
        }

        return finalDamage;
    }

    // Método para verificar proteção do Paladino
    public static boolean isProtected(BoardUnit unit) {
        return unit.hasEffect("protected_by_paladin");
    }

    // Método auxiliar para obter instância do BattleManager
    private static BattleManager getBattleManagerInstance() {
        // Esta é uma implementação simplificada - em um sistema real,
        // você teria uma referência adequada ao BattleManager
        try {
            return new BattleManager();
        } catch (Exception e) {
            return null;
        }
    }

    // Método para verificar se pode usar habilidade no alvo
    public static boolean canUseAbilityOnTarget(BoardUnit unit, int targetX, int targetY, BattleManager battleManager) {
        if ("Clérigo".equals(unit.getCardData().name)) {
            BoardUnit target = battleManager.getUnitAt(targetX, targetY);
            if (target == null || target == unit || target.isPlayer() != unit.isPlayer())
                return false;

            int distance = Math.abs(unit.getGridX() - targetX) +
                Math.abs(unit.getGridY() - targetY);
            return distance <= 2 && target.getCurrentHp() < target.getCardData().hp;
        }
        return false;
    }
}
