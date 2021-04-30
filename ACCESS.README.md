Directed Model Checking for Fast Abstraction-Refinement, submitted to IEEE ACCESS 2021

-Introduction to the implementation and the experiment of the Error-location Directed search for distance criteria Selection (EDS)

1. Preliminaries for experimental setup
- install benchexec (refer to https://github.com/sosy-lab/benchexec)
- clone or download sv-benchmarks (https://github.com/sosy-lab/sv-benchmarks.git) in the same directory with pacc_cpachecker
- copy 140 *.set files in ACCESS.targets to sv-benchmarks/c/

2. Running model checking
- benchexec cpa-lpa-[sbe,dst,dbb,dlh,dlf].xml (for five Lazy Abstraction (LA) based techniques, i.e., LA, LA.st, LA.bb, LA.lh, and LA.lf)
- benchexec cpa-bam-[bnb,dst,dbb,dlh,dlf].xml (for five Block-Abstraction Memoization (BAM) based techniques, i.e., BAM, BAM.st, BAM.bb, BAM.lh, and BAM.lf)
- benchexec cpa-lpa-mbb.xml (for LA with directed model checking using bb as the distance criteria and on-demand distance calculation, i.e., LA.bb.OD)
the model checking results are sotored in ./results/

3. Configuration files for each technique to compare
We implemented the techniques to compare in CPAchecker. Thus, each technique to compare is denoted as a configuration file for CPAchecker.
See the "Getting Started with CPAchecker" in README.md to find how to execute CPAchecker using a configuration file
- 11 configuration files for techniques to compare
  LA:		config/predicateAnalysis-SBE.properties
  LA.st:	config/predicateAnalysis-SBE-dst.properties
  LA.bb:	config/predicateAnalysis-SBE-dbb.properties
  LA.lh:	config/predicateAnalysis-SBE-dlh.properties
  LA.lf:	config/predicateAnalysis-SBE-dlf.properties
  BAM:		config/svcomp21-bam-bnb-nowit.properties
  BAM.st:	config/svcomp21-bam-dst-nowit.properties
  BAM.bb:	config/svcomp21-bam-dbb-nowit.properties
  BAB.lh:	config/svcomp21-bam-dlh-nowit.properties
  BAB.lf:	config/svcomp21-bam-dlf-nowit.properties
  LA.bb.OD:	config/predicateAnalysis-SBE-mbb.properties
Note that we do not have configuration files for LA.EDS and BAM.EDS because we manually select the distance criteria using 25 static program metrics and the distance criteria selection rule.

4. The raw results of the experiments in the submitted paper
- Five csv files in ACCESS.results directory
-- DMC.lpa.sel.csv stores results of the five LA-based techniques and LA.EDS.
--- columns
    TASK: the name of the verification task file (that includes the target verification program)
    UNREACH: ["ttt","fff"] where "ttt" indicates that the target program is a safe program, and "fff" indicates that the target program is a unsafe program
    NODES~SDEGFN: the 25 program metrics
    RET.[LPA,DST,DBB,DLH,DLF]: the model checking result of LA, LA.[st,bb,lh,lf] respectively for a target program indicating the reason for success/failure of the model checking
    CPU.[LPA,DST,DBB,DLH,DLF]: the CPU-time of the model checking of LA, LA.[st,bb,lh,lf] respectively for a target program
    SELECT: the selected distance criteria for LA.EDS (1=st,2=bb,3=lh,4=lf) for each target program
    RET.SEL: the model checking result of LA.EDS
    CPU.SEL: the CPU-time of LA.EDS
-- DMC.bam.sel.csv stores results of the five BAM-based techniques and BAM.EDS.
--- columns
    TASK: the name of the verification task file (that includes the target verification program)
    UNREACH: ["ttt","fff"] where "ttt" indicates that the target program is a safe program, and "fff" indicates that the target program is a unsafe program
    NODES~SDEGFN: the 25 program metrics
    RET.[BNB,DST,DBB,DLH,DLF]: the model checking result of BAM, BAM.[st,bb,lh,lf] respectively for a target program indicating the reason for success/failure of the model checking
    CPU.[BNB,DST,DBB,DLH,DLF]: the CPU-time of the model checking of BAM, BAM.[st,bb,lh,lf] respectively for a target program
    SELECT: the selected distance criteria for BAM.EDS (1=st,2=bb,3=lh,4=lf) for each target program
    RET.SEL: the model checking result of BAM.EDS
    CPU.SEL: the CPU-time of BAM.EDS
-- DMC.lpa.fil.csv stores results of LA.EDS.[0,10,20,30,40,50].
--- columns
    SMCC: the sum of Cyclomatic Complexity per-function for each target program
    CRT.[S0,S10,S20,S30,S40,S50]: indicating whether LA.EDS.[0,10,20,30,40,50] successes to verify a target program (1) or not (0) respectively
    CPU.[S0,S10,S20,S30,S40,S50]: the CPU-time of LA.EDS.[0,10,20,30,40,50] respectively
-- DMC.bam.fil.csv stores results of BAM.EDS.[0,10,20,30,40,50].
--- columns
    SMCC: the sum of Cyclomatic Complexity per-function for each target program
    CRT.[S0,S10,S20,S30,S40,S50]: indicating whether BAM.EDS.[0,10,20,30,40,50] successes to verify a target program (1) or not (0) respectively
    CPU.[S0,S10,S20,S30,S40,S50]: the CPU-time of BAM.EDS.[0,10,20,30,40,50] respectively
-- DMC.mem.csv stores results of LA.bb.OD and LA.bb (memory usage).
--- columns
    TASK: the name of the verification task file (that includes the target verification program)
    UNREACH: ["ttt","fff"] where "ttt" indicates that the target program is a safe program, and "fff" indicates that the target program is a unsafe program
    RET.DBB: indicating whether LA.bb successes to verify a target program (1) or not (0)
    MEM.DBB: the maximum memory usage of LA.bb for a taget program
    RET.OBB: indicating whether LA.bb.OD successes to verify a target program (1) or not (0)
    MEM.OBB: the maximum memory usage of LA.bb.OD for a target program
    
    
    
