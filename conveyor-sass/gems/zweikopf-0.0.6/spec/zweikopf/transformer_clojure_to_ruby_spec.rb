require 'spec_helper'
java_import 'clojure.lang.Util'

describe Zweikopf do
  context "when given a non-recursive hash structure" do

    #
    # Environment
    #

    let(:hash) { load_fixture(:clj_hash1) }

    #
    # Examples
    #

    it "creates a Clojure hash" do
      Zweikopf::Transformer.from_clj(hash).should eql({:a => 1, :b => 2})
    end
  end

  context "when given a recursive hash structure" do

    #
    # Environment
    #

    let(:hash) { load_fixture(:clj_deep_hash1) }

    #
    # Examples
    #

    it "creates a Clojure hash" do
      Zweikopf::Transformer.from_clj(hash).should eql({:a => 1, :b => {:c => 3, :d =>4}})
    end
  end

  context "when given a clojure array" do

    #
    # Environment
    #

    let(:clj_array) { load_fixture(:clj_array1) }

    #
    # Examples
    #

    it "creates a Ruby array" do
      Zweikopf::Transformer.from_clj(clj_array).should eql([:a, 1, :b, 2, :c, 3])
    end
  end


  context "when given a hash that contains array that has a hash as an item" do

    #
    # Environment
    #

    let(:complex_hash) { load_fixture(:complex_hash) }

    #
    # Examples
    #

    it "creates a Ruby array" do
      Zweikopf::Transformer.from_clj(complex_hash).should eql({:a => 1, :b => [2, 3, 4]})
    end
  end

end
