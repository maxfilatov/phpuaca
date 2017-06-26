# Changelog

## Versions
* 2.x: PhpStorm 2017.1+
* 1.x: PhpStorm 2016.1+ (no support)

## 2.4
* Add mock method intention / generator [#5](https://github.com/Haehnchen/idea-php-phpunit-plugin/issues/5)

## 2.3
* Pipe property declaration of setUp test method [#11](https://github.com/Haehnchen/idea-php-phpunit-plugin/issues/11) [#13](https://github.com/Haehnchen/idea-php-phpunit-plugin/issues/13) [#14](https://github.com/Haehnchen/idea-php-phpunit-plugin/issues/14) [#17](https://github.com/Haehnchen/idea-php-phpunit-plugin/issues/17)
* Migrate createMock, getMock and reveal types to chaining method detection [#11](https://github.com/Haehnchen/idea-php-phpunit-plugin/issues/11) [#13](https://github.com/Haehnchen/idea-php-phpunit-plugin/issues/13) [#14](https://github.com/Haehnchen/idea-php-phpunit-plugin/issues/14) [#17](https://github.com/Haehnchen/idea-php-phpunit-plugin/issues/17)
* Optimize completion for createMock to use chaining detection

## 2.2.1
* Add MockBuilder->getMock TypeProvider [#10](https://github.com/Haehnchen/idea-php-phpunit-plugin/issues/10)

## 2.2
* Reimplement magic Prophecy method [#8](https://github.com/Haehnchen/idea-php-phpunit-plugin/issues/8)
* Add Prophecy reveal type provider [#9](https://github.com/Haehnchen/idea-php-phpunit-plugin/issues/9)

## 2.1
* Add custom linemarker runner for units test method and classes and provide intention [#4](https://github.com/Haehnchen/idea-php-phpunit-plugin/issues/4)
* Reimplement TypeProvider in the correct way without index access [#6](https://github.com/Haehnchen/idea-php-phpunit-plugin/issues/6)

## 2.0
* Initial release fork of [maxfilatov/phpuaca](https://github.com/maxfilatov/phpuaca/)
* Support class constants in createMock method completion

## 1.3.5
* Type providers for PHPUnit and Prophecy has been temporary removed for plugin stability reasons, will be fixed soon;

## 1.3.3
* support for Prophecy property, argument and return type completion (by [Steve Müller](https://github.com/deeky666));
* multiple fixes to prevent IDE freezes during updating indices process;
* support for latest IDE versions (by [Sebastian Hopfe](https://github.com/shopfe));

## 1.3
* code navigation (go to declaration, find usages, etc.) and refactoring (rename methods);
* highlighting of incorrect method usages;
* PHPUnit type provider;
* Prophecy type provider;
* legacy tests support: method completion for mocks defined by string class name instead of class constant reference, <tt>::class</tt> (by [Thomas Schulz](https://github.com/King2500)).

### 1.2
* Fixed false activation of plugin in classes with magic methods defined in PHPDoc blocks.

## 1.1
* Added support for methods <tt>PHPUnit_Framework_TestCase::getMock</tt>, <tt>PHPUnit_Framework_TestCase::getMockClass</tt>, <tt>PHPUnit_Framework_TestCase::getMockForAbstractClass</tt> and <tt>PHPUnit_Framework_TestCase::getMockForTrait</tt>;
* Already specified methods in mock definitions aren't used in next autocomplete suggestions;
* Fixed multiple false activations of suggestion popup.

## 1.0
* Added support for methods <tt>PHPUnit_Framework_MockObject_MockBuilder::setMethods</tt> and <tt>PHPUnit_Framework_MockObject_Builder_InvocationMocker::method</tt>.