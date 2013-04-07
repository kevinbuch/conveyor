# -*- encoding: utf-8 -*-
require File.expand_path('../lib/zweikopf/version', __FILE__)

Gem::Specification.new do |gem|
  gem.authors       = ["Oleksandr Petrov"]
  gem.email         = ["oleksandr.petrov@gmail.com"]
  gem.description   = %q{Rubygem for jruby/clojure interop}
  gem.summary       = %q{Its good, try it out!}
  gem.homepage      = ""

  gem.executables   = `git ls-files -- bin/*`.split("\n").map{ |f| File.basename(f) }
  gem.files         = `git ls-files`.split("\n")
  gem.test_files    = `git ls-files -- {test,spec,features}/*`.split("\n")
  gem.name          = "zweikopf"
  gem.require_paths = ["lib"]
  gem.version       = Zweikopf::VERSION

  gem.add_development_dependency "rake"
  gem.add_development_dependency "rspec"
end
