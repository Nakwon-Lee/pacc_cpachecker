/* Generated by CIL v. 1.3.7 */
/* print_CIL_Input is true */

#line 211 "/usr/lib/gcc/x86_64-linux-gnu/4.4.3/include/stddef.h"
typedef unsigned long size_t;
#line 95 "crackaddr-bad.c"
enum bool {
    false = 0,
    true = 1
} ;
#line 339 "/usr/include/stdio.h"
extern int printf(char const   * __restrict  __format  , ...) ;
#line 413
extern int scanf(char const   * __restrict  __format  , ...)  __asm__("__isoc99_scanf")  ;
#line 127 "/usr/include/string.h"
extern  __attribute__((__nothrow__)) char *strcpy(char * __restrict  __dest , char const   * __restrict  __src )  __attribute__((__nonnull__(1,2))) ;
#line 233
extern  __attribute__((__nothrow__)) char *strchr(char const   *__s , int __c )  __attribute__((__pure__,
__nonnull__(1))) ;
#line 397
extern  __attribute__((__nothrow__)) size_t strlen(char const   *__s )  __attribute__((__pure__,
__nonnull__(1))) ;
#line 81 "/usr/include/ctype.h"
extern  __attribute__((__nothrow__)) unsigned short const   **__ctype_b_loc(void)  __attribute__((__const__)) ;
#line 89 "crackaddr-bad.c"
#pragma ccuredvararg("scanf",printf(1))
#line 98 "crackaddr-bad.c"
int ColonOkInAddr  ;
#line 99 "crackaddr-bad.c"
char *MustQuoteChars  ;
#line 153 "crackaddr-bad.c"
static char buf[31]  ;
#line 154 "crackaddr-bad.c"
static char test_buf[10]  ;
#line 130 "crackaddr-bad.c"
char *crackaddr(char *addr ) 
{ register char *p ;
  register char c ;
  int cmtlev ;
  int realcmtlev ;
  int anglelev ;
  int realanglelev ;
  int copylev ;
  int bracklev ;
  enum bool qmode ;
  enum bool realqmode ;
  enum bool skipping ;
  enum bool putgmac ;
  enum bool quoteit ;
  enum bool gotangle ;
  enum bool gotcolon ;
  register char *bp ;
  char *obp ;
  char *buflim ;
  char *bufhead ;
  char *addrhead ;
  unsigned short const   **tmp ;
  char *tmp___0 ;
  char *tmp___1 ;
  char *tmp___2 ;
  char *tmp___3 ;
  char *tmp___4 ;
  int tmp___5 ;
  register char *q ;
  char *tmp___6 ;
  char *tmp___7 ;
  char *tmp___8 ;
  unsigned short const   **tmp___9 ;
  char *tmp___10 ;
  char *tmp___11 ;
  char *tmp___12 ;
  char *tmp___13 ;
  char *tmp___14 ;
  char *tmp___15 ;
  char *tmp___16 ;
  char *tmp___17 ;
  char *tmp___18 ;
  unsigned short const   **tmp___19 ;
  char *tmp___20 ;
  char *tmp___21 ;
  register char *q___0 ;
  char *tmp___22 ;
  unsigned short const   **tmp___23 ;
  char *tmp___24 ;
  char *tmp___25 ;
  char *tmp___26 ;
  char *tmp___27 ;
  char *tmp___28 ;
  char *tmp___29 ;
  char *tmp___30 ;
  char *tmp___31 ;
  int tmp___32 ;
  char *tmp___33 ;
  char *tmp___34 ;
  char *tmp___35 ;
  char *tmp___36 ;
  char *tmp___37 ;
  char *tmp___38 ;
  int tmp___39 ;
  char *tmp___40 ;
  int tmp___41 ;
  char *tmp___42 ;
  unsigned long __cil_tmp68 ;
  unsigned long __cil_tmp69 ;
  char *__cil_tmp70 ;
  char * __restrict  __cil_tmp71 ;
  char const   * __restrict  __cil_tmp72 ;
  char const   * __restrict  __cil_tmp73 ;
  char __cil_tmp74 ;
  int __cil_tmp75 ;
  char __cil_tmp76 ;
  int __cil_tmp77 ;
  int __cil_tmp78 ;
  char __cil_tmp79 ;
  int __cil_tmp80 ;
  unsigned short const   *__cil_tmp81 ;
  unsigned short const   *__cil_tmp82 ;
  unsigned short __cil_tmp83 ;
  int __cil_tmp84 ;
  unsigned long __cil_tmp85 ;
  unsigned long __cil_tmp86 ;
  unsigned long __cil_tmp87 ;
  unsigned long __cil_tmp88 ;
  unsigned long __cil_tmp89 ;
  char const   * __restrict  __cil_tmp90 ;
  unsigned int __cil_tmp91 ;
  int __cil_tmp92 ;
  char const   * __restrict  __cil_tmp93 ;
  int __cil_tmp94 ;
  unsigned long __cil_tmp95 ;
  unsigned long __cil_tmp96 ;
  int __cil_tmp97 ;
  unsigned int __cil_tmp98 ;
  unsigned int __cil_tmp99 ;
  int __cil_tmp100 ;
  unsigned int __cil_tmp101 ;
  int __cil_tmp102 ;
  unsigned int __cil_tmp103 ;
  int __cil_tmp104 ;
  char const   * __restrict  __cil_tmp105 ;
  unsigned int __cil_tmp106 ;
  int __cil_tmp107 ;
  unsigned int __cil_tmp108 ;
  unsigned int __cil_tmp109 ;
  unsigned int __cil_tmp110 ;
  int __cil_tmp111 ;
  unsigned int __cil_tmp112 ;
  int __cil_tmp113 ;
  char const   * __restrict  __cil_tmp114 ;
  unsigned int __cil_tmp115 ;
  unsigned long __cil_tmp116 ;
  unsigned long __cil_tmp117 ;
  int __cil_tmp118 ;
  unsigned int __cil_tmp119 ;
  int __cil_tmp120 ;
  unsigned int __cil_tmp121 ;
  int __cil_tmp122 ;
  int __cil_tmp123 ;
  int __cil_tmp124 ;
  unsigned int __cil_tmp125 ;
  int *__cil_tmp126 ;
  int __cil_tmp127 ;
  unsigned int __cil_tmp128 ;
  char __cil_tmp129 ;
  int __cil_tmp130 ;
  char __cil_tmp131 ;
  int __cil_tmp132 ;
  unsigned int __cil_tmp133 ;
  unsigned int __cil_tmp134 ;
  char __cil_tmp135 ;
  int __cil_tmp136 ;
  int __cil_tmp137 ;
  char __cil_tmp138 ;
  int __cil_tmp139 ;
  unsigned short const   *__cil_tmp140 ;
  unsigned short const   *__cil_tmp141 ;
  unsigned short __cil_tmp142 ;
  int __cil_tmp143 ;
  unsigned long __cil_tmp144 ;
  unsigned long __cil_tmp145 ;
  unsigned long __cil_tmp146 ;
  unsigned long __cil_tmp147 ;
  int __cil_tmp148 ;
  char *__cil_tmp149 ;
  unsigned long __cil_tmp150 ;
  unsigned long __cil_tmp151 ;
  int __cil_tmp152 ;
  unsigned long __cil_tmp153 ;
  unsigned long __cil_tmp154 ;
  char __cil_tmp155 ;
  int __cil_tmp156 ;
  int __cil_tmp157 ;
  char __cil_tmp158 ;
  int __cil_tmp159 ;
  unsigned short const   *__cil_tmp160 ;
  unsigned short const   *__cil_tmp161 ;
  unsigned short __cil_tmp162 ;
  int __cil_tmp163 ;
  unsigned long __cil_tmp164 ;
  unsigned long __cil_tmp165 ;
  int __cil_tmp166 ;
  int *__cil_tmp167 ;
  int __cil_tmp168 ;
  unsigned int __cil_tmp169 ;
  unsigned long __cil_tmp170 ;
  unsigned long __cil_tmp171 ;
  char const   *__cil_tmp172 ;
  int __cil_tmp173 ;
  void *__cil_tmp174 ;
  unsigned long __cil_tmp175 ;
  unsigned long __cil_tmp176 ;
  unsigned int __cil_tmp177 ;
  int __cil_tmp178 ;
  unsigned int __cil_tmp179 ;
  char __cil_tmp180 ;
  int __cil_tmp181 ;
  int __cil_tmp182 ;
  char __cil_tmp183 ;
  int __cil_tmp184 ;
  unsigned short const   *__cil_tmp185 ;
  unsigned short const   *__cil_tmp186 ;
  unsigned short __cil_tmp187 ;
  int __cil_tmp188 ;
  unsigned long __cil_tmp189 ;
  unsigned long __cil_tmp190 ;
  unsigned long __cil_tmp191 ;
  unsigned long __cil_tmp192 ;
  int __cil_tmp193 ;
  unsigned long __cil_tmp194 ;
  unsigned long __cil_tmp195 ;
  char *__cil_tmp196 ;
  unsigned long __cil_tmp197 ;
  unsigned long __cil_tmp198 ;
  int __cil_tmp199 ;
  unsigned long __cil_tmp200 ;
  unsigned long __cil_tmp201 ;
  int __cil_tmp202 ;
  unsigned int __cil_tmp203 ;
  unsigned int __cil_tmp204 ;
  unsigned int __cil_tmp205 ;
  unsigned long __cil_tmp206 ;
  unsigned long __cil_tmp207 ;
  char *__cil_tmp208 ;
  char __cil_tmp209 ;
  int __cil_tmp210 ;
  char const   * __restrict  __cil_tmp211 ;
  unsigned long __cil_tmp212 ;
  unsigned long __cil_tmp213 ;
  char *__cil_tmp214 ;
  char const   * __restrict  __cil_tmp215 ;
  char const   * __restrict  __cil_tmp216 ;
  unsigned long __cil_tmp217 ;
  unsigned long __cil_tmp218 ;
  char *__cil_tmp219 ;
  unsigned long __cil_tmp220 ;
  unsigned long __cil_tmp221 ;

  {
  {
#line 144
  putgmac = (enum bool )0;
#line 145
  quoteit = (enum bool )0;
#line 146
  gotangle = (enum bool )0;
#line 147
  gotcolon = (enum bool )0;
#line 157
  __cil_tmp68 = 0 * 1UL;
#line 157
  __cil_tmp69 = (unsigned long )(test_buf) + __cil_tmp68;
#line 157
  __cil_tmp70 = (char *)__cil_tmp69;
#line 157
  __cil_tmp71 = (char * __restrict  )__cil_tmp70;
#line 157
  __cil_tmp72 = (char const   * __restrict  )"GOOD";
#line 157
  strcpy(__cil_tmp71, __cil_tmp72);
#line 159
  __cil_tmp73 = (char const   * __restrict  )"Inside crackaddr!\n";
#line 159
  printf(__cil_tmp73);
  }
  {
#line 163
  while (1) {
    while_continue: /* CIL Label */ ;
    {
#line 163
    __cil_tmp74 = *addr;
#line 163
    __cil_tmp75 = (int )__cil_tmp74;
#line 163
    if (__cil_tmp75 != 0) {
      {
#line 163
      __cil_tmp76 = *addr;
#line 163
      __cil_tmp77 = (int )__cil_tmp76;
#line 163
      __cil_tmp78 = __cil_tmp77 & -128;
#line 163
      if (__cil_tmp78 == 0) {
        {
#line 163
        tmp = __ctype_b_loc();
        }
        {
#line 163
        __cil_tmp79 = *addr;
#line 163
        __cil_tmp80 = (int )__cil_tmp79;
#line 163
        __cil_tmp81 = *tmp;
#line 163
        __cil_tmp82 = __cil_tmp81 + __cil_tmp80;
#line 163
        __cil_tmp83 = *__cil_tmp82;
#line 163
        __cil_tmp84 = (int const   )__cil_tmp83;
#line 163
        if (__cil_tmp84 & 8192) {

        } else {
#line 163
          goto while_break;
        }
        }
      } else {
#line 163
        goto while_break;
      }
      }
    } else {
#line 163
      goto while_break;
    }
    }
#line 164
    addr = addr + 1;
  }
  while_break: /* CIL Label */ ;
  }
  {
#line 172
  __cil_tmp85 = 0 * 1UL;
#line 172
  __cil_tmp86 = (unsigned long )(buf) + __cil_tmp85;
#line 172
  bufhead = (char *)__cil_tmp86;
#line 172
  bp = bufhead;
#line 173
  obp = bp;
#line 174
  __cil_tmp87 = 31UL - 7UL;
#line 174
  __cil_tmp88 = __cil_tmp87 * 1UL;
#line 174
  __cil_tmp89 = (unsigned long )(buf) + __cil_tmp88;
#line 174
  buflim = (char *)__cil_tmp89;
#line 175
  addrhead = addr;
#line 175
  p = addrhead;
#line 176
  realcmtlev = 0;
#line 176
  cmtlev = realcmtlev;
#line 176
  realanglelev = cmtlev;
#line 176
  anglelev = realanglelev;
#line 176
  copylev = anglelev;
#line 177
  bracklev = 0;
#line 178
  realqmode = (enum bool )0;
#line 178
  qmode = realqmode;
#line 180
  __cil_tmp90 = (char const   * __restrict  )"qmode = %d\n";
#line 180
  __cil_tmp91 = (unsigned int )qmode;
#line 180
  printf(__cil_tmp90, __cil_tmp91);
  }
  {
#line 182
  while (1) {
    while_continue___0: /* CIL Label */ ;
#line 182
    tmp___36 = p;
#line 182
    p = p + 1;
#line 182
    c = *tmp___36;
    {
#line 182
    __cil_tmp92 = (int )c;
#line 182
    if (__cil_tmp92 != 0) {

    } else {
#line 182
      goto while_break___0;
    }
    }
    {
#line 190
    __cil_tmp93 = (char const   * __restrict  )"c = %c\n";
#line 190
    __cil_tmp94 = (int )c;
#line 190
    printf(__cil_tmp93, __cil_tmp94);
#line 192
    __cil_tmp95 = (unsigned long )buflim;
#line 192
    __cil_tmp96 = (unsigned long )bp;
#line 192
    __cil_tmp97 = __cil_tmp96 >= __cil_tmp95;
#line 192
    __cil_tmp98 = (unsigned int )__cil_tmp97;
#line 192
    skipping = (enum bool )__cil_tmp98;
    }
#line 194
    if (copylev > 0) {
      {
#line 194
      __cil_tmp99 = (unsigned int )skipping;
#line 194
      if (! __cil_tmp99) {
#line 198
        tmp___0 = bp;
#line 198
        bp = bp + 1;
#line 198
        *tmp___0 = c;
      } else {

      }
      }
    } else {

    }
    {
#line 201
    __cil_tmp100 = (int )c;
#line 201
    if (__cil_tmp100 == 92) {
#line 204
      if (cmtlev <= 0) {
        {
#line 204
        __cil_tmp101 = (unsigned int )qmode;
#line 204
        if (! __cil_tmp101) {
#line 205
          quoteit = (enum bool )1;
        } else {

        }
        }
      } else {

      }
#line 207
      tmp___1 = p;
#line 207
      p = p + 1;
#line 207
      c = *tmp___1;
      {
#line 207
      __cil_tmp102 = (int )c;
#line 207
      if (__cil_tmp102 == 0) {
#line 210
        p = p - 1;
#line 211
        goto putg;
      } else {

      }
      }
#line 213
      if (copylev > 0) {
        {
#line 213
        __cil_tmp103 = (unsigned int )skipping;
#line 213
        if (! __cil_tmp103) {
#line 217
          tmp___2 = bp;
#line 217
          bp = bp + 1;
#line 217
          *tmp___2 = c;
        } else {

        }
        }
      } else {

      }
#line 219
      goto putg;
    } else {

    }
    }
    {
#line 223
    __cil_tmp104 = (int )c;
#line 223
    if (__cil_tmp104 == 34) {
#line 223
      if (cmtlev <= 0) {
        {
#line 225
        __cil_tmp105 = (char const   * __restrict  )"quoted string...\n";
#line 225
        printf(__cil_tmp105);
#line 226
        __cil_tmp106 = (unsigned int )qmode;
#line 226
        __cil_tmp107 = ! __cil_tmp106;
#line 226
        __cil_tmp108 = (unsigned int )__cil_tmp107;
#line 226
        qmode = (enum bool )__cil_tmp108;
        }
#line 227
        if (copylev > 0) {
          {
#line 227
          __cil_tmp109 = (unsigned int )skipping;
#line 227
          if (! __cil_tmp109) {
#line 228
            __cil_tmp110 = (unsigned int )realqmode;
#line 228
            __cil_tmp111 = ! __cil_tmp110;
#line 228
            __cil_tmp112 = (unsigned int )__cil_tmp111;
#line 228
            realqmode = (enum bool )__cil_tmp112;
          } else {

          }
          }
        } else {

        }
#line 229
        goto while_continue___0;
      } else {

      }
    } else {

    }
    }
#line 231
    if ((unsigned int )qmode) {
#line 232
      goto putg;
    } else {

    }
    {
#line 235
    __cil_tmp113 = (int )c;
#line 235
    if (__cil_tmp113 == 40) {
      {
#line 237
      __cil_tmp114 = (char const   * __restrict  )"left ( seen....\n";
#line 237
      printf(__cil_tmp114);
#line 238
      cmtlev = cmtlev + 1;
      }
      {
#line 241
      __cil_tmp115 = (unsigned int )skipping;
#line 241
      if (! __cil_tmp115) {
#line 243
        buflim = buflim - 1;
#line 244
        realcmtlev = realcmtlev + 1;
#line 245
        tmp___5 = copylev;
#line 245
        copylev = copylev + 1;
#line 245
        if (tmp___5 <= 0) {
          {
#line 247
          __cil_tmp116 = (unsigned long )bufhead;
#line 247
          __cil_tmp117 = (unsigned long )bp;
#line 247
          if (__cil_tmp117 != __cil_tmp116) {
#line 251
            tmp___3 = bp;
#line 251
            bp = bp + 1;
#line 251
            *tmp___3 = (char )' ';
          } else {

          }
          }
#line 256
          tmp___4 = bp;
#line 256
          bp = bp + 1;
#line 256
          *tmp___4 = c;
        } else {

        }
      } else {

      }
      }
    } else {

    }
    }
#line 260
    if (cmtlev > 0) {
      {
#line 262
      __cil_tmp118 = (int )c;
#line 262
      if (__cil_tmp118 == 41) {
#line 264
        cmtlev = cmtlev - 1;
#line 265
        copylev = copylev - 1;
        {
#line 266
        __cil_tmp119 = (unsigned int )skipping;
#line 266
        if (! __cil_tmp119) {
#line 268
          realcmtlev = realcmtlev - 1;
#line 269
          buflim = buflim + 1;
        } else {

        }
        }
      } else {

      }
      }
#line 272
      goto while_continue___0;
    } else {
      {
#line 274
      __cil_tmp120 = (int )c;
#line 274
      if (__cil_tmp120 == 41) {
#line 277
        if (copylev > 0) {
          {
#line 277
          __cil_tmp121 = (unsigned int )skipping;
#line 277
          if (! __cil_tmp121) {
#line 278
            bp = bp - 1;
          } else {

          }
          }
        } else {

        }
      } else {

      }
      }
    }
    {
#line 282
    __cil_tmp122 = (int )c;
#line 282
    if (__cil_tmp122 == 91) {
#line 283
      bracklev = bracklev + 1;
    } else {
      {
#line 284
      __cil_tmp123 = (int )c;
#line 284
      if (__cil_tmp123 == 93) {
#line 285
        bracklev = bracklev - 1;
      } else {

      }
      }
    }
    }
    {
#line 288
    __cil_tmp124 = (int )c;
#line 288
    if (__cil_tmp124 == 58) {
#line 288
      if (anglelev <= 0) {
#line 288
        if (bracklev <= 0) {
          {
#line 288
          __cil_tmp125 = (unsigned int )gotcolon;
#line 288
          if (! __cil_tmp125) {
            {
#line 288
            __cil_tmp126 = & ColonOkInAddr;
#line 288
            __cil_tmp127 = *__cil_tmp126;
#line 288
            __cil_tmp128 = (unsigned int )__cil_tmp127;
#line 288
            if (! __cil_tmp128) {
              {
#line 300
              __cil_tmp129 = *p;
#line 300
              __cil_tmp130 = (int )__cil_tmp129;
#line 300
              if (__cil_tmp130 == 58) {
#line 300
                goto _L;
              } else {
                {
#line 300
                __cil_tmp131 = *p;
#line 300
                __cil_tmp132 = (int )__cil_tmp131;
#line 300
                if (__cil_tmp132 == 46) {
                  _L: /* CIL Label */ 
#line 302
                  if (cmtlev <= 0) {
                    {
#line 302
                    __cil_tmp133 = (unsigned int )qmode;
#line 302
                    if (! __cil_tmp133) {
#line 303
                      quoteit = (enum bool )1;
                    } else {

                    }
                    }
                  } else {

                  }
#line 304
                  if (copylev > 0) {
                    {
#line 304
                    __cil_tmp134 = (unsigned int )skipping;
#line 304
                    if (! __cil_tmp134) {
#line 308
                      tmp___6 = bp;
#line 308
                      bp = bp + 1;
#line 308
                      *tmp___6 = c;
#line 311
                      tmp___7 = bp;
#line 311
                      bp = bp + 1;
#line 311
                      *tmp___7 = *p;
                    } else {

                    }
                    }
                  } else {

                  }
#line 313
                  p = p + 1;
#line 314
                  goto putg;
                } else {

                }
                }
              }
              }
#line 317
              gotcolon = (enum bool )1;
#line 319
              bp = bufhead;
#line 320
              if ((unsigned int )quoteit) {
#line 324
                tmp___8 = bp;
#line 324
                bp = bp + 1;
#line 324
                *tmp___8 = (char )'\"';
#line 327
                p = p - 1;
                {
#line 329
                while (1) {
                  while_continue___1: /* CIL Label */ ;
#line 329
                  p = p - 1;
                  {
#line 329
                  __cil_tmp135 = *p;
#line 329
                  __cil_tmp136 = (int )__cil_tmp135;
#line 329
                  __cil_tmp137 = __cil_tmp136 & -128;
#line 329
                  if (__cil_tmp137 == 0) {
                    {
#line 329
                    tmp___9 = __ctype_b_loc();
                    }
                    {
#line 329
                    __cil_tmp138 = *p;
#line 329
                    __cil_tmp139 = (int )__cil_tmp138;
#line 329
                    __cil_tmp140 = *tmp___9;
#line 329
                    __cil_tmp141 = __cil_tmp140 + __cil_tmp139;
#line 329
                    __cil_tmp142 = *__cil_tmp141;
#line 329
                    __cil_tmp143 = (int const   )__cil_tmp142;
#line 329
                    if (__cil_tmp143 & 8192) {

                    } else {
#line 329
                      goto while_break___1;
                    }
                    }
                  } else {
#line 329
                    goto while_break___1;
                  }
                  }
#line 330
                  goto while_continue___1;
                }
                while_break___1: /* CIL Label */ ;
                }
#line 331
                p = p + 1;
              } else {

              }
#line 333
              q = addrhead;
              {
#line 333
              while (1) {
                while_continue___2: /* CIL Label */ ;
                {
#line 333
                __cil_tmp144 = (unsigned long )p;
#line 333
                __cil_tmp145 = (unsigned long )q;
#line 333
                if (__cil_tmp145 < __cil_tmp144) {

                } else {
#line 333
                  goto while_break___2;
                }
                }
#line 335
                tmp___10 = q;
#line 335
                q = q + 1;
#line 335
                c = *tmp___10;
                {
#line 336
                __cil_tmp146 = (unsigned long )buflim;
#line 336
                __cil_tmp147 = (unsigned long )bp;
#line 336
                if (__cil_tmp147 < __cil_tmp146) {
#line 338
                  if ((unsigned int )quoteit) {
                    {
#line 338
                    __cil_tmp148 = (int )c;
#line 338
                    if (__cil_tmp148 == 34) {
#line 341
                      tmp___11 = bp;
#line 341
                      bp = bp + 1;
#line 341
                      *tmp___11 = (char )'\\';
                    } else {

                    }
                    }
                  } else {

                  }
#line 344
                  tmp___12 = bp;
#line 344
                  bp = bp + 1;
#line 344
                  *tmp___12 = c;
                } else {

                }
                }
              }
              while_break___2: /* CIL Label */ ;
              }
#line 348
              if ((unsigned int )quoteit) {
                {
#line 350
                __cil_tmp149 = bufhead + 1;
#line 350
                __cil_tmp150 = (unsigned long )__cil_tmp149;
#line 350
                __cil_tmp151 = (unsigned long )bp;
#line 350
                if (__cil_tmp151 == __cil_tmp150) {
#line 351
                  bp = bp - 1;
                } else {
#line 355
                  tmp___13 = bp;
#line 355
                  bp = bp + 1;
#line 355
                  *tmp___13 = (char )'\"';
                }
                }
                {
#line 357
                while (1) {
                  while_continue___3: /* CIL Label */ ;
#line 357
                  tmp___15 = p;
#line 357
                  p = p + 1;
#line 357
                  c = *tmp___15;
                  {
#line 357
                  __cil_tmp152 = (int )c;
#line 357
                  if (__cil_tmp152 != 58) {

                  } else {
#line 357
                    goto while_break___3;
                  }
                  }
                  {
#line 359
                  __cil_tmp153 = (unsigned long )buflim;
#line 359
                  __cil_tmp154 = (unsigned long )bp;
#line 359
                  if (__cil_tmp154 < __cil_tmp153) {
#line 362
                    tmp___14 = bp;
#line 362
                    bp = bp + 1;
#line 362
                    *tmp___14 = c;
                  } else {

                  }
                  }
                }
                while_break___3: /* CIL Label */ ;
                }
#line 367
                tmp___16 = bp;
#line 367
                bp = bp + 1;
#line 367
                *tmp___16 = c;
              } else {

              }
              {
#line 371
              while (1) {
                while_continue___4: /* CIL Label */ ;
                {
#line 371
                __cil_tmp155 = *p;
#line 371
                __cil_tmp156 = (int )__cil_tmp155;
#line 371
                __cil_tmp157 = __cil_tmp156 & -128;
#line 371
                if (__cil_tmp157 == 0) {
                  {
#line 371
                  tmp___19 = __ctype_b_loc();
                  }
                  {
#line 371
                  __cil_tmp158 = *p;
#line 371
                  __cil_tmp159 = (int )__cil_tmp158;
#line 371
                  __cil_tmp160 = *tmp___19;
#line 371
                  __cil_tmp161 = __cil_tmp160 + __cil_tmp159;
#line 371
                  __cil_tmp162 = *__cil_tmp161;
#line 371
                  __cil_tmp163 = (int const   )__cil_tmp162;
#line 371
                  if (__cil_tmp163 & 8192) {
                    {
#line 371
                    __cil_tmp164 = (unsigned long )buflim;
#line 371
                    __cil_tmp165 = (unsigned long )bp;
#line 371
                    if (__cil_tmp165 < __cil_tmp164) {

                    } else {
#line 371
                      goto while_break___4;
                    }
                    }
                  } else {
#line 371
                    goto while_break___4;
                  }
                  }
                } else {
#line 371
                  goto while_break___4;
                }
                }
#line 375
                tmp___17 = bp;
#line 375
                bp = bp + 1;
#line 375
                tmp___18 = p;
#line 375
                p = p + 1;
#line 375
                *tmp___17 = *tmp___18;
              }
              while_break___4: /* CIL Label */ ;
              }
#line 377
              copylev = 0;
#line 378
              quoteit = (enum bool )0;
#line 378
              putgmac = quoteit;
#line 379
              bufhead = bp;
#line 380
              addrhead = p;
#line 381
              goto while_continue___0;
            } else {

            }
            }
          } else {

          }
          }
        } else {

        }
      } else {

      }
    } else {

    }
    }
    {
#line 384
    __cil_tmp166 = (int )c;
#line 384
    if (__cil_tmp166 == 59) {
#line 384
      if (copylev <= 0) {
        {
#line 384
        __cil_tmp167 = & ColonOkInAddr;
#line 384
        __cil_tmp168 = *__cil_tmp167;
#line 384
        __cil_tmp169 = (unsigned int )__cil_tmp168;
#line 384
        if (! __cil_tmp169) {
          {
#line 386
          __cil_tmp170 = (unsigned long )buflim;
#line 386
          __cil_tmp171 = (unsigned long )bp;
#line 386
          if (__cil_tmp171 < __cil_tmp170) {
#line 389
            tmp___20 = bp;
#line 389
            bp = bp + 1;
#line 389
            *tmp___20 = c;
          } else {

          }
          }
        } else {

        }
        }
      } else {

      }
    } else {

    }
    }
    {
#line 393
    __cil_tmp172 = (char const   *)MustQuoteChars;
#line 393
    __cil_tmp173 = (int )c;
#line 393
    tmp___21 = strchr(__cil_tmp172, __cil_tmp173);
    }
    {
#line 393
    __cil_tmp174 = (void *)0;
#line 393
    __cil_tmp175 = (unsigned long )__cil_tmp174;
#line 393
    __cil_tmp176 = (unsigned long )tmp___21;
#line 393
    if (__cil_tmp176 != __cil_tmp175) {
#line 402
      if (cmtlev <= 0) {
        {
#line 402
        __cil_tmp177 = (unsigned int )qmode;
#line 402
        if (! __cil_tmp177) {
#line 403
          quoteit = (enum bool )1;
        } else {

        }
        }
      } else {

      }
    } else {

    }
    }
    {
#line 407
    __cil_tmp178 = (int )c;
#line 407
    if (__cil_tmp178 == 60) {
#line 412
      if ((unsigned int )gotangle) {
#line 413
        quoteit = (enum bool )1;
      } else {

      }
#line 414
      gotangle = (enum bool )1;
#line 417
      anglelev = 1;
      {
#line 418
      __cil_tmp179 = (unsigned int )skipping;
#line 418
      if (! __cil_tmp179) {
#line 419
        realanglelev = 1;
      } else {

      }
      }
#line 421
      bp = bufhead;
#line 422
      if ((unsigned int )quoteit) {
#line 426
        tmp___22 = bp;
#line 426
        bp = bp + 1;
#line 426
        *tmp___22 = (char )'\"';
#line 429
        p = p - 1;
        {
#line 430
        while (1) {
          while_continue___5: /* CIL Label */ ;
#line 430
          p = p - 1;
          {
#line 430
          __cil_tmp180 = *p;
#line 430
          __cil_tmp181 = (int )__cil_tmp180;
#line 430
          __cil_tmp182 = __cil_tmp181 & -128;
#line 430
          if (__cil_tmp182 == 0) {
            {
#line 430
            tmp___23 = __ctype_b_loc();
            }
            {
#line 430
            __cil_tmp183 = *p;
#line 430
            __cil_tmp184 = (int )__cil_tmp183;
#line 430
            __cil_tmp185 = *tmp___23;
#line 430
            __cil_tmp186 = __cil_tmp185 + __cil_tmp184;
#line 430
            __cil_tmp187 = *__cil_tmp186;
#line 430
            __cil_tmp188 = (int const   )__cil_tmp187;
#line 430
            if (__cil_tmp188 & 8192) {

            } else {
#line 430
              goto while_break___5;
            }
            }
          } else {
#line 430
            goto while_break___5;
          }
          }
#line 431
          goto while_continue___5;
        }
        while_break___5: /* CIL Label */ ;
        }
#line 432
        p = p + 1;
      } else {

      }
#line 434
      q___0 = addrhead;
      {
#line 434
      while (1) {
        while_continue___6: /* CIL Label */ ;
        {
#line 434
        __cil_tmp189 = (unsigned long )p;
#line 434
        __cil_tmp190 = (unsigned long )q___0;
#line 434
        if (__cil_tmp190 < __cil_tmp189) {

        } else {
#line 434
          goto while_break___6;
        }
        }
#line 436
        tmp___24 = q___0;
#line 436
        q___0 = q___0 + 1;
#line 436
        c = *tmp___24;
        {
#line 437
        __cil_tmp191 = (unsigned long )buflim;
#line 437
        __cil_tmp192 = (unsigned long )bp;
#line 437
        if (__cil_tmp192 < __cil_tmp191) {
#line 439
          if ((unsigned int )quoteit) {
            {
#line 439
            __cil_tmp193 = (int )c;
#line 439
            if (__cil_tmp193 == 34) {
#line 442
              tmp___25 = bp;
#line 442
              bp = bp + 1;
#line 442
              *tmp___25 = (char )'\\';
            } else {

            }
            }
          } else {

          }
#line 445
          tmp___26 = bp;
#line 445
          bp = bp + 1;
#line 445
          *tmp___26 = c;
        } else {

        }
        }
      }
      while_break___6: /* CIL Label */ ;
      }
#line 448
      if ((unsigned int )quoteit) {
        {
#line 450
        __cil_tmp194 = 1 * 1UL;
#line 450
        __cil_tmp195 = (unsigned long )(buf) + __cil_tmp194;
#line 450
        __cil_tmp196 = (char *)__cil_tmp195;
#line 450
        __cil_tmp197 = (unsigned long )__cil_tmp196;
#line 450
        __cil_tmp198 = (unsigned long )bp;
#line 450
        if (__cil_tmp198 == __cil_tmp197) {
#line 451
          bp = bp - 1;
        } else {
#line 455
          tmp___27 = bp;
#line 455
          bp = bp + 1;
#line 455
          *tmp___27 = (char )'\"';
        }
        }
        {
#line 456
        while (1) {
          while_continue___7: /* CIL Label */ ;
#line 456
          tmp___29 = p;
#line 456
          p = p + 1;
#line 456
          c = *tmp___29;
          {
#line 456
          __cil_tmp199 = (int )c;
#line 456
          if (__cil_tmp199 != 60) {

          } else {
#line 456
            goto while_break___7;
          }
          }
          {
#line 458
          __cil_tmp200 = (unsigned long )buflim;
#line 458
          __cil_tmp201 = (unsigned long )bp;
#line 458
          if (__cil_tmp201 < __cil_tmp200) {
#line 461
            tmp___28 = bp;
#line 461
            bp = bp + 1;
#line 461
            *tmp___28 = c;
          } else {

          }
          }
        }
        while_break___7: /* CIL Label */ ;
        }
#line 465
        tmp___30 = bp;
#line 465
        bp = bp + 1;
#line 465
        *tmp___30 = c;
      } else {

      }
#line 467
      copylev = 0;
#line 468
      quoteit = (enum bool )0;
#line 468
      putgmac = quoteit;
#line 469
      goto while_continue___0;
    } else {

    }
    }
    {
#line 472
    __cil_tmp202 = (int )c;
#line 472
    if (__cil_tmp202 == 62) {
#line 474
      if (anglelev > 0) {
#line 476
        anglelev = anglelev - 1;
        {
#line 477
        __cil_tmp203 = (unsigned int )skipping;
#line 477
        if (! __cil_tmp203) {
#line 479
          realanglelev = realanglelev - 1;
#line 480
          buflim = buflim + 1;
        } else {

        }
        }
      } else {
        {
#line 483
        __cil_tmp204 = (unsigned int )skipping;
#line 483
        if (! __cil_tmp204) {
#line 486
          if (copylev > 0) {
#line 487
            bp = bp - 1;
          } else {

          }
#line 488
          quoteit = (enum bool )1;
#line 489
          goto while_continue___0;
        } else {

        }
        }
      }
#line 491
      tmp___32 = copylev;
#line 491
      copylev = copylev + 1;
#line 491
      if (tmp___32 <= 0) {
#line 494
        tmp___31 = bp;
#line 494
        bp = bp + 1;
#line 494
        *tmp___31 = c;
      } else {

      }
#line 495
      goto while_continue___0;
    } else {

    }
    }
    putg: 
#line 500
    if (copylev <= 0) {
      {
#line 500
      __cil_tmp205 = (unsigned int )putgmac;
#line 500
      if (! __cil_tmp205) {
        {
#line 502
        __cil_tmp206 = (unsigned long )bufhead;
#line 502
        __cil_tmp207 = (unsigned long )bp;
#line 502
        if (__cil_tmp207 > __cil_tmp206) {
          {
#line 502
          __cil_tmp208 = bp + -1;
#line 502
          __cil_tmp209 = *__cil_tmp208;
#line 502
          __cil_tmp210 = (int )__cil_tmp209;
#line 502
          if (__cil_tmp210 == 41) {
#line 505
            tmp___33 = bp;
#line 505
            bp = bp + 1;
#line 505
            *tmp___33 = (char )' ';
          } else {

          }
          }
        } else {

        }
        }
#line 508
        tmp___34 = bp;
#line 508
        bp = bp + 1;
#line 508
        *tmp___34 = (char)-127;
#line 511
        tmp___35 = bp;
#line 511
        bp = bp + 1;
#line 511
        *tmp___35 = (char )'g';
#line 512
        putgmac = (enum bool )1;
      } else {

      }
      }
    } else {

    }
    {
#line 514
    __cil_tmp211 = (char const   * __restrict  )"Buf = %s\n";
#line 514
    __cil_tmp212 = 0 * 1UL;
#line 514
    __cil_tmp213 = (unsigned long )(buf) + __cil_tmp212;
#line 514
    __cil_tmp214 = (char *)__cil_tmp213;
#line 514
    printf(__cil_tmp211, __cil_tmp214);
    }
  }
  while_break___0: /* CIL Label */ ;
  }
#line 518
  if ((unsigned int )realqmode) {
#line 521
    tmp___37 = bp;
#line 521
    bp = bp + 1;
#line 521
    *tmp___37 = (char )'\"';
  } else {

  }
  {
#line 522
  while (1) {
    while_continue___8: /* CIL Label */ ;
#line 522
    tmp___39 = realcmtlev;
#line 522
    realcmtlev = realcmtlev - 1;
#line 522
    if (tmp___39 > 0) {

    } else {
#line 522
      goto while_break___8;
    }
#line 525
    tmp___38 = bp;
#line 525
    bp = bp + 1;
#line 525
    *tmp___38 = (char )')';
  }
  while_break___8: /* CIL Label */ ;
  }
  {
#line 526
  while (1) {
    while_continue___9: /* CIL Label */ ;
#line 526
    tmp___41 = realanglelev;
#line 526
    realanglelev = realanglelev - 1;
#line 526
    if (tmp___41 > 0) {

    } else {
#line 526
      goto while_break___9;
    }
#line 529
    tmp___40 = bp;
#line 529
    bp = bp + 1;
#line 529
    *tmp___40 = (char )'>';
  }
  while_break___9: /* CIL Label */ ;
  }
  {
#line 532
  tmp___42 = bp;
#line 532
  bp = bp + 1;
#line 532
  *tmp___42 = (char )'\000';
#line 534
  __cil_tmp215 = (char const   * __restrict  )"test_buf should equal GOOD\n";
#line 534
  printf(__cil_tmp215);
#line 535
  __cil_tmp216 = (char const   * __restrict  )"test_buf = %s\n";
#line 535
  __cil_tmp217 = 0 * 1UL;
#line 535
  __cil_tmp218 = (unsigned long )(test_buf) + __cil_tmp217;
#line 535
  __cil_tmp219 = (char *)__cil_tmp218;
#line 535
  printf(__cil_tmp216, __cil_tmp219);
  }
  {
#line 537
  __cil_tmp220 = 0 * 1UL;
#line 537
  __cil_tmp221 = (unsigned long )(buf) + __cil_tmp220;
#line 537
  return ((char *)__cil_tmp221);
  }
}
}
#line 541 "crackaddr-bad.c"
int main(void) 
{ char address[100] ;
  char *res_addr ;
  size_t tmp ;
  char const   * __restrict  __cil_tmp4 ;
  char const   * __restrict  __cil_tmp5 ;
  char const   * __restrict  __cil_tmp6 ;
  char const   * __restrict  __cil_tmp7 ;
  unsigned long __cil_tmp8 ;
  unsigned long __cil_tmp9 ;
  char *__cil_tmp10 ;
  unsigned long __cil_tmp11 ;
  unsigned long __cil_tmp12 ;
  char *__cil_tmp13 ;
  char const   * __restrict  __cil_tmp14 ;
  char const   *__cil_tmp15 ;
  char const   * __restrict  __cil_tmp16 ;

  {
  {
#line 546
  __cil_tmp4 = (char const   * __restrict  )"Type 1 or 0 to allow or disallow colons in email address:\n";
#line 546
  printf(__cil_tmp4);
#line 547
  __cil_tmp5 = (char const   * __restrict  )"%d";
#line 547
  scanf(__cil_tmp5, & ColonOkInAddr);
#line 548
  MustQuoteChars = (char *)"@,;:\\()[].\'";
#line 550
  __cil_tmp6 = (char const   * __restrict  )"Enter email address:\n";
#line 550
  printf(__cil_tmp6);
#line 551
  __cil_tmp7 = (char const   * __restrict  )"%99s";
#line 551
  __cil_tmp8 = 0 * 1UL;
#line 551
  __cil_tmp9 = (unsigned long )(address) + __cil_tmp8;
#line 551
  __cil_tmp10 = (char *)__cil_tmp9;
#line 551
  scanf(__cil_tmp7, __cil_tmp10);
#line 553
  __cil_tmp11 = 0 * 1UL;
#line 553
  __cil_tmp12 = (unsigned long )(address) + __cil_tmp11;
#line 553
  __cil_tmp13 = (char *)__cil_tmp12;
#line 553
  res_addr = crackaddr(__cil_tmp13);
#line 554
  __cil_tmp14 = (char const   * __restrict  )"result = %s\n";
#line 554
  printf(__cil_tmp14, res_addr);
#line 555
  __cil_tmp15 = (char const   *)res_addr;
#line 555
  tmp = strlen(__cil_tmp15);
#line 555
  __cil_tmp16 = (char const   * __restrict  )"buf len = %d\n";
#line 555
  printf(__cil_tmp16, tmp);
  }
#line 557
  return (0);
}
}
