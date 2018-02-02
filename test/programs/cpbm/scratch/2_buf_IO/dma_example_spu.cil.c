/* Generated by CIL v. 1.3.7 */
/* print_CIL_Input is true */

#line 44 "../dma_example.h"
struct _control_block {
   unsigned long long in_addr ;
   unsigned long long out_addr ;
   unsigned int num_elements_per_spe ;
   unsigned int id ;
   unsigned int pad[2] ;
};
#line 44 "../dma_example.h"
typedef struct _control_block control_block_t;
#line 339 "/usr/include/stdio.h"
extern int printf(char const   * __restrict  __format  , ...) ;
#line 203 "../spu_mfcio.h"
extern void mfc_put(void volatile   *ls , unsigned int ea , unsigned int size , unsigned int tag ,
                    unsigned int tid , unsigned int rid ) ;
#line 211
extern void mfc_get(void volatile   *ls , unsigned int ea , unsigned int size , unsigned int tag ,
                    unsigned int tid , unsigned int rid ) ;
#line 252
extern void mfc_write_tag_mask(unsigned int mask ) ;
#line 270
extern void mfc_read_tag_status_all() ;
#line 27 "dma_example_spu.c"
float local_in_buffers[2][4096]  __attribute__((__aligned__(128)))  ;
#line 35 "dma_example_spu.c"
float local_out_buffers[2][4096]  __attribute__((__aligned__(128)))  ;
#line 42 "dma_example_spu.c"
control_block_t control_block  __attribute__((__aligned__(128)))  ;
#line 104
extern int __VERIFIER_nondet_int() ;
#line 140
extern int ( /* missing proto */  assert)() ;
#line 75 "dma_example_spu.c"
int spu_main(unsigned long long speid  __attribute__((__unused__)) , unsigned long long argp ,
             unsigned long long envp  __attribute__((__unused__)) ) 
{ unsigned int tags[2] ;
  unsigned long long in_addr ;
  unsigned long long out_addr ;
  unsigned int i ;
  unsigned int num_chunks ;
  int cur_buf ;
  int next_buf ;
  int tmp ;
  int tmp___0 ;
  int tmp___1 ;
  int tmp___2 ;
  int tmp___3 ;
  int tmp___4 ;
  int tmp___5 ;
  unsigned long __cil_tmp18 ;
  unsigned long __cil_tmp19 ;
  unsigned long __cil_tmp20 ;
  unsigned long __cil_tmp21 ;
  unsigned long __cil_tmp22 ;
  unsigned long __cil_tmp23 ;
  unsigned int __cil_tmp24 ;
  char const   * __restrict  __cil_tmp25 ;
  unsigned long __cil_tmp26 ;
  unsigned long __cil_tmp27 ;
  unsigned int __cil_tmp28 ;
  char const   * __restrict  __cil_tmp29 ;
  void volatile   *__cil_tmp30 ;
  unsigned int __cil_tmp31 ;
  unsigned int __cil_tmp32 ;
  unsigned long __cil_tmp33 ;
  unsigned long __cil_tmp34 ;
  unsigned int __cil_tmp35 ;
  control_block_t *__cil_tmp36 ;
  unsigned long __cil_tmp37 ;
  unsigned long __cil_tmp38 ;
  unsigned long __cil_tmp39 ;
  unsigned long __cil_tmp40 ;
  unsigned long __cil_tmp41 ;
  unsigned long __cil_tmp42 ;
  unsigned long __cil_tmp43 ;
  unsigned long __cil_tmp44 ;
  unsigned long __cil_tmp45 ;
  unsigned long __cil_tmp46 ;
  unsigned long __cil_tmp47 ;
  unsigned int __cil_tmp48 ;
  int __cil_tmp49 ;
  unsigned int __cil_tmp50 ;
  control_block_t *__cil_tmp51 ;
  unsigned long __cil_tmp52 ;
  unsigned long __cil_tmp53 ;
  unsigned long __cil_tmp54 ;
  unsigned long __cil_tmp55 ;
  float *__cil_tmp56 ;
  void volatile   *__cil_tmp57 ;
  unsigned int __cil_tmp58 ;
  unsigned long __cil_tmp59 ;
  unsigned int __cil_tmp60 ;
  unsigned long __cil_tmp61 ;
  unsigned long __cil_tmp62 ;
  unsigned int __cil_tmp63 ;
  unsigned long __cil_tmp64 ;
  unsigned long long __cil_tmp65 ;
  unsigned long __cil_tmp66 ;
  unsigned long __cil_tmp67 ;
  unsigned int __cil_tmp68 ;
  unsigned long __cil_tmp69 ;
  unsigned long __cil_tmp70 ;
  unsigned int __cil_tmp71 ;
  int __cil_tmp72 ;
  unsigned long __cil_tmp73 ;
  unsigned long __cil_tmp74 ;
  unsigned int __cil_tmp75 ;
  int __cil_tmp76 ;
  unsigned long __cil_tmp77 ;
  unsigned long __cil_tmp78 ;
  unsigned long __cil_tmp79 ;
  unsigned long __cil_tmp80 ;
  float *__cil_tmp81 ;
  void volatile   *__cil_tmp82 ;
  unsigned int __cil_tmp83 ;
  unsigned long __cil_tmp84 ;
  unsigned int __cil_tmp85 ;
  unsigned long __cil_tmp86 ;
  unsigned long __cil_tmp87 ;
  unsigned int __cil_tmp88 ;
  unsigned long __cil_tmp89 ;
  unsigned long __cil_tmp90 ;
  unsigned int __cil_tmp91 ;
  int __cil_tmp92 ;
  unsigned int __cil_tmp93 ;
  unsigned long __cil_tmp94 ;
  unsigned long __cil_tmp95 ;
  unsigned long __cil_tmp96 ;
  unsigned long __cil_tmp97 ;
  float *__cil_tmp98 ;
  void volatile   *__cil_tmp99 ;
  unsigned int __cil_tmp100 ;
  unsigned long __cil_tmp101 ;
  unsigned int __cil_tmp102 ;
  unsigned long __cil_tmp103 ;
  unsigned long __cil_tmp104 ;
  unsigned int __cil_tmp105 ;
  unsigned long __cil_tmp106 ;
  unsigned long long __cil_tmp107 ;
  unsigned long __cil_tmp108 ;
  unsigned long long __cil_tmp109 ;
  unsigned long __cil_tmp110 ;
  unsigned long __cil_tmp111 ;
  unsigned int __cil_tmp112 ;
  int __cil_tmp113 ;
  unsigned int __cil_tmp114 ;
  unsigned long __cil_tmp115 ;
  unsigned long __cil_tmp116 ;
  unsigned long __cil_tmp117 ;
  unsigned long __cil_tmp118 ;
  float *__cil_tmp119 ;
  void volatile   *__cil_tmp120 ;
  unsigned int __cil_tmp121 ;
  unsigned long __cil_tmp122 ;
  unsigned int __cil_tmp123 ;
  unsigned long __cil_tmp124 ;
  unsigned long __cil_tmp125 ;
  unsigned int __cil_tmp126 ;
  unsigned long __cil_tmp127 ;
  unsigned long __cil_tmp128 ;
  unsigned int __cil_tmp129 ;
  int __cil_tmp130 ;
  unsigned int __cil_tmp131 ;

  {
#line 94
  __cil_tmp18 = 0 * 4UL;
#line 94
  __cil_tmp19 = (unsigned long )(tags) + __cil_tmp18;
#line 94
  *((unsigned int *)__cil_tmp19) = 0U;
#line 95
  __cil_tmp20 = 1 * 4UL;
#line 95
  __cil_tmp21 = (unsigned long )(tags) + __cil_tmp20;
#line 95
  *((unsigned int *)__cil_tmp21) = 1U;
  {
#line 97
  __cil_tmp22 = 0 * 4UL;
#line 97
  __cil_tmp23 = (unsigned long )(tags) + __cil_tmp22;
#line 97
  __cil_tmp24 = *((unsigned int *)__cil_tmp23);
#line 97
  if (__cil_tmp24 == 4294967295U) {
    {
#line 99
    __cil_tmp25 = (char const   * __restrict  )"SPU ERROR, unable to reserve tag\n";
#line 99
    printf(__cil_tmp25);
    }
#line 100
    return (1);
  } else {
    {
#line 97
    __cil_tmp26 = 1 * 4UL;
#line 97
    __cil_tmp27 = (unsigned long )(tags) + __cil_tmp26;
#line 97
    __cil_tmp28 = *((unsigned int *)__cil_tmp27);
#line 97
    if (__cil_tmp28 == 4294967295U) {
      {
#line 99
      __cil_tmp29 = (char const   * __restrict  )"SPU ERROR, unable to reserve tag\n";
#line 99
      printf(__cil_tmp29);
      }
#line 100
      return (1);
    } else {

    }
    }
  }
  }
  {
#line 103
  __cil_tmp30 = (void volatile   *)(& control_block);
#line 103
  __cil_tmp31 = (unsigned int )argp;
#line 103
  __cil_tmp32 = (unsigned int )32UL;
#line 103
  __cil_tmp33 = 0 * 4UL;
#line 103
  __cil_tmp34 = (unsigned long )(tags) + __cil_tmp33;
#line 103
  __cil_tmp35 = *((unsigned int *)__cil_tmp34);
#line 103
  mfc_get(__cil_tmp30, __cil_tmp31, __cil_tmp32, __cil_tmp35, 0U, 0U);
#line 104
  tmp = __VERIFIER_nondet_int();
#line 104
  __cil_tmp36 = & control_block;
#line 104
  *((unsigned long long *)__cil_tmp36) = (unsigned long long )tmp;
#line 104
  tmp___0 = __VERIFIER_nondet_int();
#line 104
  __cil_tmp37 = (unsigned long )(& control_block) + 8;
#line 104
  *((unsigned long long *)__cil_tmp37) = (unsigned long long )tmp___0;
#line 104
  tmp___1 = __VERIFIER_nondet_int();
#line 104
  __cil_tmp38 = (unsigned long )(& control_block) + 16;
#line 104
  *((unsigned int *)__cil_tmp38) = (unsigned int )tmp___1;
#line 104
  tmp___2 = __VERIFIER_nondet_int();
#line 104
  __cil_tmp39 = (unsigned long )(& control_block) + 20;
#line 104
  *((unsigned int *)__cil_tmp39) = (unsigned int )tmp___2;
#line 104
  tmp___3 = __VERIFIER_nondet_int();
#line 104
  __cil_tmp40 = 0 * 4UL;
#line 104
  __cil_tmp41 = 24 + __cil_tmp40;
#line 104
  __cil_tmp42 = (unsigned long )(& control_block) + __cil_tmp41;
#line 104
  *((unsigned int *)__cil_tmp42) = (unsigned int )tmp___3;
#line 104
  tmp___4 = __VERIFIER_nondet_int();
#line 104
  __cil_tmp43 = 1 * 4UL;
#line 104
  __cil_tmp44 = 24 + __cil_tmp43;
#line 104
  __cil_tmp45 = (unsigned long )(& control_block) + __cil_tmp44;
#line 104
  *((unsigned int *)__cil_tmp45) = (unsigned int )tmp___4;
#line 107
  __cil_tmp46 = 0 * 4UL;
#line 107
  __cil_tmp47 = (unsigned long )(tags) + __cil_tmp46;
#line 107
  __cil_tmp48 = *((unsigned int *)__cil_tmp47);
#line 107
  __cil_tmp49 = 1 << __cil_tmp48;
#line 107
  __cil_tmp50 = (unsigned int )__cil_tmp49;
#line 107
  mfc_write_tag_mask(__cil_tmp50);
#line 108
  mfc_read_tag_status_all();
#line 111
  cur_buf = 0;
#line 115
  __cil_tmp51 = & control_block;
#line 115
  in_addr = *((unsigned long long *)__cil_tmp51);
#line 118
  __cil_tmp52 = 0 * 4UL;
#line 118
  __cil_tmp53 = cur_buf * 16384UL;
#line 118
  __cil_tmp54 = __cil_tmp53 + __cil_tmp52;
#line 118
  __cil_tmp55 = (unsigned long )(local_in_buffers) + __cil_tmp54;
#line 118
  __cil_tmp56 = (float *)__cil_tmp55;
#line 118
  __cil_tmp57 = (void volatile   *)__cil_tmp56;
#line 118
  __cil_tmp58 = (unsigned int )in_addr;
#line 118
  __cil_tmp59 = 4096UL * 4UL;
#line 118
  __cil_tmp60 = (unsigned int )__cil_tmp59;
#line 118
  __cil_tmp61 = cur_buf * 4UL;
#line 118
  __cil_tmp62 = (unsigned long )(tags) + __cil_tmp61;
#line 118
  __cil_tmp63 = *((unsigned int *)__cil_tmp62);
#line 118
  mfc_get(__cil_tmp57, __cil_tmp58, __cil_tmp60, __cil_tmp63, 0U, 0U);
#line 122
  __cil_tmp64 = 4096UL * 4UL;
#line 122
  __cil_tmp65 = (unsigned long long )__cil_tmp64;
#line 122
  in_addr = in_addr + __cil_tmp65;
#line 126
  __cil_tmp66 = (unsigned long )(& control_block) + 8;
#line 126
  out_addr = *((unsigned long long *)__cil_tmp66);
#line 130
  __cil_tmp67 = (unsigned long )(& control_block) + 16;
#line 130
  __cil_tmp68 = *((unsigned int *)__cil_tmp67);
#line 130
  num_chunks = __cil_tmp68 / 4096U;
#line 137
  i = 1U;
  }
  {
#line 137
  while (1) {
    while_continue: /* CIL Label */ ;
#line 137
    if (i < num_chunks) {

    } else {
#line 137
      goto while_break;
    }
#line 140
    if (cur_buf == 0) {
#line 140
      tmp___5 = 1;
    } else
#line 140
    if (cur_buf == 1) {
#line 140
      tmp___5 = 1;
    } else {
#line 140
      tmp___5 = 0;
    }
    {
#line 140
    assert(tmp___5);
#line 141
    __cil_tmp69 = 0 * 4UL;
#line 141
    __cil_tmp70 = (unsigned long )(tags) + __cil_tmp69;
#line 141
    __cil_tmp71 = *((unsigned int *)__cil_tmp70);
#line 141
    __cil_tmp72 = __cil_tmp71 == 0U;
#line 141
    assert(__cil_tmp72);
#line 142
    __cil_tmp73 = 1 * 4UL;
#line 142
    __cil_tmp74 = (unsigned long )(tags) + __cil_tmp73;
#line 142
    __cil_tmp75 = *((unsigned int *)__cil_tmp74);
#line 142
    __cil_tmp76 = __cil_tmp75 == 1U;
#line 142
    assert(__cil_tmp76);
#line 146
    next_buf = cur_buf ^ 1;
#line 151
    __cil_tmp77 = 0 * 4UL;
#line 151
    __cil_tmp78 = next_buf * 16384UL;
#line 151
    __cil_tmp79 = __cil_tmp78 + __cil_tmp77;
#line 151
    __cil_tmp80 = (unsigned long )(local_in_buffers) + __cil_tmp79;
#line 151
    __cil_tmp81 = (float *)__cil_tmp80;
#line 151
    __cil_tmp82 = (void volatile   *)__cil_tmp81;
#line 151
    __cil_tmp83 = (unsigned int )in_addr;
#line 151
    __cil_tmp84 = 4096UL * 4UL;
#line 151
    __cil_tmp85 = (unsigned int )__cil_tmp84;
#line 151
    __cil_tmp86 = next_buf * 4UL;
#line 151
    __cil_tmp87 = (unsigned long )(tags) + __cil_tmp86;
#line 151
    __cil_tmp88 = *((unsigned int *)__cil_tmp87);
#line 151
    mfc_get(__cil_tmp82, __cil_tmp83, __cil_tmp85, __cil_tmp88, 0U, 0U);
#line 155
    __cil_tmp89 = next_buf * 4UL;
#line 155
    __cil_tmp90 = (unsigned long )(tags) + __cil_tmp89;
#line 155
    __cil_tmp91 = *((unsigned int *)__cil_tmp90);
#line 155
    __cil_tmp92 = 1 << __cil_tmp91;
#line 155
    __cil_tmp93 = (unsigned int )__cil_tmp92;
#line 155
    mfc_write_tag_mask(__cil_tmp93);
#line 163
    mfc_read_tag_status_all();
#line 173
    __cil_tmp94 = 0 * 4UL;
#line 173
    __cil_tmp95 = cur_buf * 16384UL;
#line 173
    __cil_tmp96 = __cil_tmp95 + __cil_tmp94;
#line 173
    __cil_tmp97 = (unsigned long )(local_out_buffers) + __cil_tmp96;
#line 173
    __cil_tmp98 = (float *)__cil_tmp97;
#line 173
    __cil_tmp99 = (void volatile   *)__cil_tmp98;
#line 173
    __cil_tmp100 = (unsigned int )out_addr;
#line 173
    __cil_tmp101 = 4096UL * 4UL;
#line 173
    __cil_tmp102 = (unsigned int )__cil_tmp101;
#line 173
    __cil_tmp103 = cur_buf * 4UL;
#line 173
    __cil_tmp104 = (unsigned long )(tags) + __cil_tmp103;
#line 173
    __cil_tmp105 = *((unsigned int *)__cil_tmp104);
#line 173
    mfc_put(__cil_tmp99, __cil_tmp100, __cil_tmp102, __cil_tmp105, 0U, 0U);
#line 177
    __cil_tmp106 = 4096UL * 4UL;
#line 177
    __cil_tmp107 = (unsigned long long )__cil_tmp106;
#line 177
    in_addr = in_addr + __cil_tmp107;
#line 178
    __cil_tmp108 = 4096UL * 4UL;
#line 178
    __cil_tmp109 = (unsigned long long )__cil_tmp108;
#line 178
    out_addr = out_addr + __cil_tmp109;
#line 181
    cur_buf = next_buf;
#line 137
    i = i + 1U;
    }
  }
  while_break: /* CIL Label */ ;
  }
  {
#line 185
  __cil_tmp110 = cur_buf * 4UL;
#line 185
  __cil_tmp111 = (unsigned long )(tags) + __cil_tmp110;
#line 185
  __cil_tmp112 = *((unsigned int *)__cil_tmp111);
#line 185
  __cil_tmp113 = 1 << __cil_tmp112;
#line 185
  __cil_tmp114 = (unsigned int )__cil_tmp113;
#line 185
  mfc_write_tag_mask(__cil_tmp114);
#line 186
  mfc_read_tag_status_all();
#line 194
  __cil_tmp115 = 0 * 4UL;
#line 194
  __cil_tmp116 = cur_buf * 16384UL;
#line 194
  __cil_tmp117 = __cil_tmp116 + __cil_tmp115;
#line 194
  __cil_tmp118 = (unsigned long )(local_out_buffers) + __cil_tmp117;
#line 194
  __cil_tmp119 = (float *)__cil_tmp118;
#line 194
  __cil_tmp120 = (void volatile   *)__cil_tmp119;
#line 194
  __cil_tmp121 = (unsigned int )out_addr;
#line 194
  __cil_tmp122 = 4096UL * 4UL;
#line 194
  __cil_tmp123 = (unsigned int )__cil_tmp122;
#line 194
  __cil_tmp124 = cur_buf * 4UL;
#line 194
  __cil_tmp125 = (unsigned long )(tags) + __cil_tmp124;
#line 194
  __cil_tmp126 = *((unsigned int *)__cil_tmp125);
#line 194
  mfc_put(__cil_tmp120, __cil_tmp121, __cil_tmp123, __cil_tmp126, 0U, 0U);
#line 198
  __cil_tmp127 = cur_buf * 4UL;
#line 198
  __cil_tmp128 = (unsigned long )(tags) + __cil_tmp127;
#line 198
  __cil_tmp129 = *((unsigned int *)__cil_tmp128);
#line 198
  __cil_tmp130 = 1 << __cil_tmp129;
#line 198
  __cil_tmp131 = (unsigned int )__cil_tmp130;
#line 198
  mfc_write_tag_mask(__cil_tmp131);
#line 199
  mfc_read_tag_status_all();
  }
#line 209
  return (0);
}
}
