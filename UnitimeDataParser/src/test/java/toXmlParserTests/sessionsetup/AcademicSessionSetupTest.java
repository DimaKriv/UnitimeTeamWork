package toXmlParserTests.sessionsetup;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import toXmlParser.sessionsetup.AcademicSessionSetup;

import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * Created by lll on 22-Apr-18.
 */
class AcademicSessionSetupTest {


    AcademicSessionSetup academicSessionSetup;

    @BeforeEach
    void setup() throws SQLException {
        academicSessionSetup = new AcademicSessionSetup("1123");
    }


    @Test
    void testGetCorrectDateWithGetDateInFormat() throws SQLException {


            when(academicSessionSetup.getResultSetDayAndWeek(any(),any()).next()).thenReturn(true).thenReturn(false);
            when(academicSessionSetup.getResultSetDayAndWeek(any(),any()).getString("paev")).thenReturn("0");
            when(academicSessionSetup.getResultSetDayAndWeek(any(),any()).getString("nadal")).thenReturn("1");

            assertEquals(academicSessionSetup.getDateInFormat(1, 1), "26/02/2018");


    }

    @Test
    void testCheckIfEveryDatePatternIssed() throws SQLException {


        when(academicSessionSetup.getResultSetDayAndWeek(any(),any()).next()).thenReturn(true).thenReturn(false);
        when(academicSessionSetup.getResultSetDayAndWeek(any(),any()).getString("paev")).thenReturn("4");
        when(academicSessionSetup.getResultSetDayAndWeek(any(),any()).getString("nadal")).thenReturn("2");

        assertEquals(academicSessionSetup.getDateInFormat(1, 1), "03/03/2018");

    }



}