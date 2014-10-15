/* C++ code produced by gperf version 3.0.3 */
/* Command-line: /Applications/Xcode.app/Contents/Developer/Toolchains/XcodeDefault.xctoolchain/usr/bin/gperf -L C++ -E -t /private/var/folders/7n/l6vc139d0c7bswg8b84j45s80000gn/T/astjohn/titanium_google_plus-generated/KrollGeneratedBindings.gperf  */
/* Computed positions: -k'' */

#line 3 "/private/var/folders/7n/l6vc139d0c7bswg8b84j45s80000gn/T/astjohn/titanium_google_plus-generated/KrollGeneratedBindings.gperf"


#include <string.h>
#include <v8.h>
#include <KrollBindings.h>

#include "com.sitata.googleplus.TitaniumGooglePlusModule.h"
#include "com.sitata.googleplus.ExampleProxy.h"


#line 14 "/private/var/folders/7n/l6vc139d0c7bswg8b84j45s80000gn/T/astjohn/titanium_google_plus-generated/KrollGeneratedBindings.gperf"
struct titanium::bindings::BindEntry;
/* maximum key range = 13, duplicates = 0 */

class TitaniumGooglePlusBindings
{
private:
  static inline unsigned int hash (const char *str, unsigned int len);
public:
  static struct titanium::bindings::BindEntry *lookupGeneratedInit (const char *str, unsigned int len);
};

inline /*ARGSUSED*/
unsigned int
TitaniumGooglePlusBindings::hash (register const char *str, register unsigned int len)
{
  return len;
}

struct titanium::bindings::BindEntry *
TitaniumGooglePlusBindings::lookupGeneratedInit (register const char *str, register unsigned int len)
{
  enum
    {
      TOTAL_KEYWORDS = 2,
      MIN_WORD_LENGTH = 34,
      MAX_WORD_LENGTH = 46,
      MIN_HASH_VALUE = 34,
      MAX_HASH_VALUE = 46
    };

  static struct titanium::bindings::BindEntry wordlist[] =
    {
      {""}, {""}, {""}, {""}, {""}, {""}, {""}, {""}, {""},
      {""}, {""}, {""}, {""}, {""}, {""}, {""}, {""}, {""},
      {""}, {""}, {""}, {""}, {""}, {""}, {""}, {""}, {""},
      {""}, {""}, {""}, {""}, {""}, {""}, {""},
#line 17 "/private/var/folders/7n/l6vc139d0c7bswg8b84j45s80000gn/T/astjohn/titanium_google_plus-generated/KrollGeneratedBindings.gperf"
      {"com.sitata.googleplus.ExampleProxy", ::com::sitata::googleplus::titaniumgoogleplus::ExampleProxy::bindProxy, ::com::sitata::googleplus::titaniumgoogleplus::ExampleProxy::dispose},
      {""}, {""}, {""}, {""}, {""}, {""}, {""}, {""}, {""},
      {""}, {""},
#line 16 "/private/var/folders/7n/l6vc139d0c7bswg8b84j45s80000gn/T/astjohn/titanium_google_plus-generated/KrollGeneratedBindings.gperf"
      {"com.sitata.googleplus.TitaniumGooglePlusModule", ::com::sitata::googleplus::TitaniumGooglePlusModule::bindProxy, ::com::sitata::googleplus::TitaniumGooglePlusModule::dispose}
    };

  if (len <= MAX_WORD_LENGTH && len >= MIN_WORD_LENGTH)
    {
      unsigned int key = hash (str, len);

      if (key <= MAX_HASH_VALUE)
        {
          register const char *s = wordlist[key].name;

          if (*str == *s && !strcmp (str + 1, s + 1))
            return &wordlist[key];
        }
    }
  return 0;
}
#line 18 "/private/var/folders/7n/l6vc139d0c7bswg8b84j45s80000gn/T/astjohn/titanium_google_plus-generated/KrollGeneratedBindings.gperf"

