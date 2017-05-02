package de.espend.idea.php.phpunit.type.utils;

import de.espend.idea.php.phpunit.type.MockProphecyTypeProvider;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.function.Function;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class ProphecyTypeUtil {
    /**
     * #元#M#C\FooTest.prophesizeƒ#K#C\Foo.class => #K#C\Foo.class
     */
    public static String getProphesizeSignatureFromTypes(@NotNull Collection<String> types) {
        return types.stream().filter(s -> {
            boolean b = s.startsWith("#" + MockProphecyTypeProvider.CHAR);
            if (!b) {
                return false;
            }

            int i = s.indexOf(MockProphecyTypeProvider.TRIM_KEY);
            return i >= 0 && s.substring(2, i).endsWith("prophesize");
        }).findFirst().map(s ->
            s.substring(s.indexOf(MockProphecyTypeProvider.TRIM_KEY) + 1)
        ).orElse(null);
    }
}
