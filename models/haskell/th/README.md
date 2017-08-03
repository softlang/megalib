# Template Haskell

Recommended order for reading:
   1. HaskellPlatform.megal and HaskellAbstractSyntax.megal:
      This files describe the Abstract Syntax of Haskell and the GHC technology.
   2. SyntaxQ.megal:
      Description of the Template Haskell Abstract Syntax Tree elements.
   3. Splice.megal and Quotation.megal:
      Core functionality of Template Haskell like splice and quote.
   4. QuasiQuotation.megal:
      The QuasiQuotation mechanism for Template Haskell allowing embedding DSL in
      Haskell.
   5. TemplateHaskellLibrary.megal:
      Library for Template Haskell providing useful functions like pretty-printing

# TODO

1. New documentation format with description and rationale.
2. Some things should be restructured such as the fact enumerations for languages and roles, which one should notice while adding rationale.
3. Some links seem to be outdated.
4. Major issue: You have to model GHC before being able to do TH properly.