PHPUnit Autocomplete Assistant
==============================
[![Version](http://phpstorm.espend.de/badge/7722/version)](https://plugins.jetbrains.com/plugin/7722)
[![Downloads](http://phpstorm.espend.de/badge/7722/downloads)](https://plugins.jetbrains.com/plugin/7722)
[![Downloads last month](http://phpstorm.espend.de/badge/7722/last-month)](https://plugins.jetbrains.com/plugin/7722)

PhpStorm plugin to provide smart autocomplete, code navigation and refactoring features for mocked class methods.

Supported all versions of PhpStorm since 7.1.

Feature list
------------

* method autocomplete for class, abstract class and trait mock objects;
  * type providers: `getMock`, `getMockForAbstractClass`, etc. will return mock object with methods of mocking class and `PHPUnit_Framework_MockObject_MockObject`;
  * supported PHPUnit methods:
    * `\PHPUnit\Framework\MockObject\MockBuilder::setMethods`
    * `\PHPUnit\Framework\TestCase::getMock`
    * `\PHPUnit\Framework\TestCase::getMockClass`
    * `\PHPUnit\Framework\TestCase::getMockForAbstractClass`
    * `\PHPUnit\Framework\TestCase::getMockForTrait`
    * `\PHPUnit\Framework\MockObject_Builder_InvocationMocker::method`
* code navigation (go to declaration, find usages, etc.) and refactoring (rename methods);
* highlighting of incorrect method usages;
* Prophecy support.

Examples
--------
Mock creation:

![PHPUnit_Framework_MockObject_MockBuilder::setMethods](https://raw.githubusercontent.com/maxfilatov/phpuaca/master/img/pmb.png)

Mock usage:

![PHPUnit_Framework_MockObject_Builder_InvocationMocker::method](https://raw.githubusercontent.com/maxfilatov/phpuaca/master/img/pim.png)

Releases
--------
* Latest (JDK 8 is required) [phpuaca-1.3.6.jar](https://github.com/maxfilatov/phpuaca/releases/download/1.3.6/phpuaca-1.3.6.jar)

Installation
------------
Stable version, JetBrains repository:
* Go to `PhpStorm -> Preferences... -> Plugins -> Browse repositories ...` and search for PHPUnit Autocomplete Assistant plugin
* Restart PhpStorm

Latest version, installation from disk:
* Go to `PhpStorm -> Preferences... -> Plugins -> Install plugin from disk...` and choose jar file
* Restart PhpStorm
