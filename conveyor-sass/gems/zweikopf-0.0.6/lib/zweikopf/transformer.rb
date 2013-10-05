java_import "clojure.lang.IPersistentMap"
java_import "clojure.lang.IPersistentVector"
java_import "clojure.lang.IPersistentList"
java_import "clojure.lang.Keyword"

module Zweikopf
  module Transformer

    def self.from_ruby(obj, &block)
      if Primitive.is_primitive_type?(obj)
        if obj.respond_to?(:to_java)
          obj.to_java
        else
          obj
        end
      elsif obj.is_a?(::Hash)
        Hash.from_ruby(obj, &block)
      elsif obj.is_a?(::Array)
        Array.from_ruby(obj, &block)
      elsif obj.is_a?(::Symbol)
        Keyword.from_ruby(obj)
      elsif !block.nil?
        from_ruby(yield(obj))
      else
        obj
      end
    end # self.from_ruby

    def self.from_clj(obj, &block)
      if Primitive.is_primitive_type?(obj)
        obj
      elsif obj.is_a?(IPersistentMap)
        Hash.from_clj(obj, &block)
      elsif obj.is_a?(IPersistentVector || IPersistentList)
        Array.from_clj(obj, &block)
      elsif obj.is_a?(::Keyword)
        Keyword.from_clj(obj)
      elsif !block.nil?
        from_clj(yield(obj))
      else
        obj
      end
    end # self.from_clj

  end # Transformer
end # Zweikopf
