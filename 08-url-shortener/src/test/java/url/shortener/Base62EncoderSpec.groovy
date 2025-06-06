package url.shortener

import spock.lang.Specification
import spock.lang.Unroll

class Base62EncoderSpec extends Specification {

    private Base62Encoder base62Encoder

    void setup() {
        base62Encoder = new Base62Encoder()
    }

    @Unroll
    def "should return encoded URL when given database ID #input -> #expected"() {
        expect:
        base62Encoder.encode(input) == expected

        where:
        input       || expected
        1_000_000   || "29C4"
        10_000_000  || "KSxf"
        20_000_000  || "euuL1"
        30_000_000  || "yMs12"
    }
}
