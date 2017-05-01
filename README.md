IntelliJ IDEA / PhpStorm PHPUnit Enhancement
==============================

[![Build Status](https://travis-ci.org/Haehnchen/idea-php-phpunit-plugin.svg?branch=master)](https://travis-ci.org/Haehnchen/idea-php-phpunit-plugin)
[![Version](http://phpstorm.espend.de/badge/9674/version)](https://plugins.jetbrains.com/plugin/9674)
[![Downloads](http://phpstorm.espend.de/badge/9674/downloads)](https://plugins.jetbrains.com/plugin/9674)
[![Downloads last month](http://phpstorm.espend.de/badge/9674/last-month)](https://plugins.jetbrains.com/plugin/9674)

PhpStorm plugin to provide smart autocomplete, code navigation and refactoring features for mocked class methods. Supported all versions of PhpStorm since 2017.1

Key         | Value
----------- | -----------
Plugin Url  | https://plugins.jetbrains.com/plugin/9674
ID          | de.espend.idea.php.phpunit
Changelog   | [CHANGELOG](CHANGELOG.md)
Origin Fork | [maxfilatov/phpuaca](https://github.com/maxfilatov/phpuaca/)

Installation
------------
Stable version, JetBrains repository:
* Go to `PhpStorm -> Preferences... -> Plugins -> Browse repositories ...` and search for PHPUnit Autocomplete Assistant plugin
* Restart PhpStorm

Feature list
------------

* method autocomplete for class, abstract class and trait mock objects;
  * type providers: `getMock`, `getMockForAbstractClass`, etc. will return mock object with methods of mocking class and `PHPUnit_Framework_MockObject_MockObject`;
  * supported PHPUnit methods:
    * `PHPUnit_Framework_MockObject_MockBuilder::setMethods`
    * `PHPUnit_Framework_TestCase::getMock`
    * `PHPUnit_Framework_TestCase::getMockClass`
    * `PHPUnit_Framework_TestCase::getMockForAbstractClass`
    * `PHPUnit_Framework_TestCase::getMockForTrait`
    * `PHPUnit_Framework_MockObject_Builder_InvocationMocker::method` 
* code navigation (go to declaration, find usages, etc.) and refactoring (rename methods);
* highlighting of incorrect method usages;
* Prophecy support.

Examples
--------

![PHPUnit_Framework_MockObject_MockBuilder::setMethods](https://jetbrains-plugins.s3.amazonaws.com/9674/screenshot_16946.png)

![PHPUnit_Framework_MockObject_Builder_InvocationMocker::method](https://jetbrains-plugins.s3.amazonaws.com/9674/screenshot_16945.png)

![PHPUnit_Framework_MockObject_Builder_InvocationMocker::method](https://jetbrains-plugins.s3.amazonaws.com/9674/screenshot_16944.png)

![PHPUnit Runner LineMarker](https://jetbrains-plugins.s3.amazonaws.com/9674/screenshot_16951.png)
