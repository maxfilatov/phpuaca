PHPUnit Autocomplete Assistant
==============================
PhpStorm plugin to provide smart autocomplete for mocked class methods.

Supported versions of PhpStorm: 7.1, 8.

Features are available for method definition arguments of these PHPUnit methods:
* `PHPUnit_Framework_MockObject_MockBuilder::setMethods`
* `PHPUnit_Framework_TestCase::getMock`
* `PHPUnit_Framework_TestCase::getMockClass`
* `PHPUnit_Framework_TestCase::getMockForAbstractClass`
* `PHPUnit_Framework_TestCase::getMockForTrait`
* `PHPUnit_Framework_MockObject_Builder_InvocationMocker::method`

Example #1: autocomplete for argument `$methods` of `PHPUnit_Framework_MockObject_MockBuilder::setMethods`

![PHPUnit_Framework_MockObject_MockBuilder::setMethods](https://raw.githubusercontent.com/maxfilatov/phpuaca/master/img/pmb.png)

Example #2: autocomplete for argument `$constraint` of `PHPUnit_Framework_MockObject_Builder_InvocationMocker::method`

![PHPUnit_Framework_MockObject_Builder_InvocationMocker::method](https://raw.githubusercontent.com/maxfilatov/phpuaca/master/img/pim.png)

Releases
--------
* Stable [phpuaca-1.2.1.jar](https://github.com/maxfilatov/phpuaca/releases/download/1.2.1/phpuaca-1.2.1.jar)

Installation
------------
* Go to `PhpStorm -> Preferences... -> Plugins -> Install plugin from disk...` and choose jar file
* Restart PhpStorm
