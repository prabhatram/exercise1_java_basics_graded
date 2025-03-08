package prog2.exercise1.graded;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.Period;
import java.util.Random;
import java.lang.reflect.Method;


public class MarsXTest {
    
    private MarsX marsX;
    private MarsX.TripType tripType;
    private static final Random random = new Random();

    @Before
    public void setUp() {
        // Randomly select a trip type
        MarsX.TripType[] tripTypes = MarsX.TripType.values();
        tripType = tripTypes[random.nextInt(tripTypes.length)];

        // Generate a random passenger name
        String[] names = {"John Doe", "Jane Smith", "Michael Brown", "Emily Davis"};
        String passengerName = names[random.nextInt(names.length)];

        // Generate a random birthdate (between 1980 and 2010)
        int birthYear = random.nextInt(31) + 1980;  // Year between 1980-2010
        LocalDate dateOfBirth = LocalDate.of(birthYear, random.nextInt(12) + 1, random.nextInt(28) + 1);

        // Generate random departure and return dates (in 2025)
        LocalDate dateOfDeparture = LocalDate.of(2025, random.nextInt(12) + 1, random.nextInt(28) + 1);
        LocalDate dateOfReturn = dateOfDeparture.plusDays(8 + random.nextInt(30));  // Ensuring at least 7 days stay

        // Generate random number of co-passengers and children
        int numberOfCoPassengers = random.nextInt(5);
        int numberOfChildren = numberOfCoPassengers > 0 ? random.nextInt(numberOfCoPassengers) : 0;

        // Generate random insurance details
        int insuranceNumber = random.nextInt(100000);
        String[] insuranceCompanies = {"SpaceShield", "Galactic Cover", "OrbitCare"};
        String insuranceCompany = insuranceCompanies[random.nextInt(insuranceCompanies.length)];
        String insuranceContactNumber = "555-" + (1000 + random.nextInt(9000));

        // Create a new MarsX object with randomized values
        marsX = new MarsX(tripType, passengerName, dateOfBirth, dateOfDeparture, dateOfReturn,
                numberOfCoPassengers, numberOfChildren, 0, "None", insuranceNumber, insuranceCompany, insuranceContactNumber);
    }

    // ✅ Test TripType Constructor & Getters
    @Test
    public void testTripTypeEnumConstructorAndGetters() {
        assertNotNull(tripType.getTripType());
        assertTrue(tripType.getBasePrice() > 0);
    }

    // ✅ Test TripType toString()
    @Test
    public void testTripTypeToString() {
        String output = tripType.toString();
        assertTrue(output.contains(tripType.getTripType()));
        assertTrue(output.contains("Ticket Price: " + tripType.getBasePrice()));
    }

    // ✅ Test MarsX Constructor
    @Test
    public void testMarsXConstructor() {
        assertNotNull(marsX.getPassengerName());
        assertNotNull(marsX.getDateOfBirth());
        assertNotNull(marsX.getDateOfDeparture());
        assertNotNull(marsX.getDateOfReturn());
        assertTrue(marsX.getNumberOfCoPassengers() >= 0);
        assertTrue(marsX.getNumberOfChildren() >= 0);
        assertNotNull(marsX.getInsuranceCompany());
        assertNotNull(marsX.getInsuranceContactNumber());
    }

    // ✅ Test MarsX Getters & Setters
    @Test
    public void testMarsXSettersAndGetters() {
        marsX.setPassengerName("Alex Johnson");
        marsX.setNumberOfChildren(3);
        marsX.setInsuranceCompany("MarsSafe");

        assertEquals("Alex Johnson", marsX.getPassengerName());
        assertEquals(3, marsX.getNumberOfChildren());
        assertEquals("MarsSafe", marsX.getInsuranceCompany());
    }

    @Test
    public void testCalculateAgeUsingReflection() throws Exception {
        Method method = MarsX.class.getDeclaredMethod("calculateAge", LocalDate.class);
        method.setAccessible(true);

        int expectedAge = LocalDate.now().getYear() - marsX.getDateOfBirth().getYear();
        int actualAge = (int) method.invoke(marsX, marsX.getDateOfBirth());

        assertEquals(expectedAge, actualAge);
    }

    // ✅ Test Date is After Departure Using Reflection
    @Test
    public void testDateAfterDepartureUsingReflection() throws Exception {
        Method method = MarsX.class.getDeclaredMethod("dateAfterDeparture");
        method.setAccessible(true);

        boolean result = (boolean) method.invoke(marsX);
        assertTrue(result); // Return date should be after departure
    }

    // ✅ Test Calculate Price After Tax Using Reflection
    @Test
    public void testCalculatePriceAfterTaxUsingReflection() throws Exception {
        Method method = MarsX.class.getDeclaredMethod("calculatePriceAfterTax", double.class);
        method.setAccessible(true);

        // Compute expected price after tax
        double basePrice = marsX.getTripType().getBasePrice();
        double pricePerPassenger = basePrice;

        // Apply correct surcharges based on trip type
        switch(marsX.getTripType()) {
            case BASIC:
                pricePerPassenger += (basePrice * 0.05) + (basePrice * 0.2);
                break;
            case STANDARD:
                pricePerPassenger += (basePrice * 0.05) + (basePrice * 0.15) + (basePrice * 0.2);
                break;
            case PREMIUM:
                pricePerPassenger += (basePrice * 0.05) + (basePrice * 0.15) + (basePrice * 0.2) + (basePrice * 0.2);
                break;
            default:
                break;
        }

        // Now calculate the expected price after tax
        double priceBeforeTax = (pricePerPassenger * (marsX.getTotalPassengers() - marsX.getNumberOfChildren())) + (pricePerPassenger * marsX.getNumberOfChildren() * (1 - 0.2));
        double expectedPriceAfterTax = priceBeforeTax * 1.25;
        
        // Call the actual method
        double actualPriceAfterTax = (double) method.invoke(marsX, pricePerPassenger);
        
        // Assert the values match
        assertEquals(expectedPriceAfterTax, actualPriceAfterTax, 0.01);
    }

    // ✅ Test Calculate Total Ticket Price Using Reflection
    @Test
    public void testCalculateTotalTicketPriceUsingReflection() throws Exception {
        Method method = MarsX.class.getDeclaredMethod("calculateTotalTicketPrice");
        method.setAccessible(true);

        marsX.setTotalPassengers(3); // Ensure total passengers is set

        double totalPrice = (double) method.invoke(marsX);
        assertTrue(totalPrice > 0);
    }

    @Test
    public void testMarsXToString() {
        String output = marsX.toString();
        assertTrue(output.contains(tripType.getTripType()));
        assertTrue(output.contains(marsX.getPassengerName()));
        assertTrue(output.contains(marsX.getInsuranceCompany()));
        assertTrue(output.contains(marsX.getInsuranceContactNumber()));
    }

}
