name := "rdf2-dev-makebean"

description := "Rough Diamond Framework Development Environment Make Bean Command Plugin"

organization := "jp.rough_diamond"

version := "2.0.1"

scalaVersion := "2.9.1"

sbtPlugin := true 

addSbtPlugin("jp.rough_diamond" % "rdf2-dev-core" % "2.0.2")

libraryDependencies +=  "org.scalatest"         %%  "scalatest"         % "1.7.1"   % "test"

libraryDependencies +=  "commons-io"            %   "commons-io"        % "2.1"

libraryDependencies +=  "org.apache.commons"    %   "commons-lang3"     % "3.1"     % "test"

libraryDependencies +=  "org.apache.velocity"   %   "velocity"          % "1.7"
