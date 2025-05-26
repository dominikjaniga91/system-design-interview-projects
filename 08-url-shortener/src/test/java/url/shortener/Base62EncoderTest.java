package url.shortener;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class Base62EncoderTest {

    private Base62Encoder base62Encoder;

    @BeforeEach
    void setUp() {
        base62Encoder = new Base62Encoder();
    }

    @ParameterizedTest
    @CsvSource({
            "1_000_000, 29C4",
            "10_000_000, KSxf",
            "20_000_000, euuL1",
            "30_000_000, yMs12"
    })
    void shouldReturnEncodedUrl_whenGivenDatabaseId(int input, String expected) {
        String actual = base62Encoder.encode(input);

        Assertions.assertThat(actual).isEqualTo(expected);
    }
}
