# Changelog

## Versions
* 2.0: PhpStorm 2017.1+
* 1.0: PhpStorm 2016.1+ (no support)

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