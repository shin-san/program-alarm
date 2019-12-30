package au.com.advent.programalarm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

public class ProgramAlarm {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProgramAlarm.class);

    private static HashMap<Integer,Integer> intCodeCommands = new HashMap<>();

    private static final int INSTRUCTION_STEPS = 4;
    private static String[] intCodes;

    public static void main(String[] args) {

        getCommands();

        LOGGER.info("Value in position 0: {}", startIntCodeProgram(12,2));

        resetMemory(intCodes);

        LOGGER.info("Answer is: {}", determineNounAndVerb(19690720));
    }

    private static void getCommands() {

        final File commands = new File("src/main/resources/input.txt");

        try (BufferedReader br = new BufferedReader(new FileReader(commands))) {

            intCodes = br.readLine().split(",");
//            String[] intCodes = {"1","0","0","0","99"};
//            String[] intCodes = {"2","4","4","5","99","0"};
//            String[] intCodes = {"1","1","1","4","99","5","6","0","99"};
            resetMemory(intCodes);
        } catch (Exception ex) {
            LOGGER.error("Exception occurred: {0}", ex);
            throw new RuntimeException("Exception occurred");
        }
    }

    private static int determineNounAndVerb(int expectedOutput) {

        int result = 0;

        for (int noun = 0; noun <= 99; noun++) {
            for (int verb = 0; verb <= 99; verb++) {
                result = startIntCodeProgram(noun,verb);

                if (result == expectedOutput) {
                    LOGGER.info("Found the noun: {} and verb: {} resulting to {}", noun, verb, result);
                    return 100 * noun + verb;
                } else {
                    resetMemory(intCodes);
                }
            }
        }

        return 0;
    }

    private static int startIntCodeProgram(int noun, int verb) {

        intCodeCommands.put(1, noun);
        intCodeCommands.put(2, verb);

        calculateIntCode();

        return intCodeCommands.get(0);
    }

    private static void calculateIntCode() {

        int totalValue = 0;

        for (int i = 0;  i <= intCodeCommands.size(); i+=INSTRUCTION_STEPS) {

            switch (intCodeCommands.get(i)) {
                case 99:
                    LOGGER.debug("Opcode command 99 has been detected. Terminating program...");
                    return;

                case 1:
                    totalValue = intCodeCommands.get(intCodeCommands.get(i + 1)) + intCodeCommands.get(intCodeCommands.get(i + 2));
                    break;

                case 2:
                    totalValue = intCodeCommands.get(intCodeCommands.get(i + 1)) * intCodeCommands.get(intCodeCommands.get(i + 2));
                    break;
            }

            LOGGER.debug("Replacing value in position {} from {} to {}", (i + 3), intCodeCommands.get(intCodeCommands.get(i + 3)), totalValue);
            intCodeCommands.put(intCodeCommands.get(i + 3),totalValue);
        }
    }

    private static void resetMemory(String[] intCodes) {

        LOGGER.debug("Resetting memory...");

        int iterator = 0;

        for (String intCode : intCodes) {
            intCodeCommands.put(iterator, Integer.parseInt(intCode));
            iterator++;
        }
    }
}
