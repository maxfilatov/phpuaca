PHPUnit Autocomplete Assistant
==============================
PhpStorm plugin to provide smart autocomplete for mocked class methods.

Supported all versions of PhpStorm since 7.1

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
* Latest [phpuaca-1.3.0-unstable-3.jar](https://github.com/maxfilatov/phpuaca/releases/download/1.3.0-unstable-3/phpuaca-1.3.0-unstable-3.jar)

Installation
------------
Stable version:
* Go to `PhpStorm -> Preferences... -> Plugins -> Browse repositories ...` and search for PHPUnit Autocomplete Assistant plugin
* Restart PhpStorm

Latest version:
* Go to `PhpStorm -> Preferences... -> Plugins -> Install plugin from disk...` and choose jar file
* Restart PhpStorm
