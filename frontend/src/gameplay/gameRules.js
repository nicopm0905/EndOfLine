
export const COLOR_PREFIX = {
  BLUE: 'CB',
  GREEN: 'CG',
  RED: 'CR',
  YELLOW: 'CY',
  MAGENTA: 'CM',
  ORANGE: 'CO',
  WHITE: 'CW',
  VIOLET: 'CV',
};

export const getRotationStyle = (orientation) => {
  switch (orientation) {
    case 'E': return { transform: 'rotate(90deg)' };
    case 'S': return { transform: 'rotate(180deg)' };
    case 'W': return { transform: 'rotate(270deg)' };
    case 'N': 
    default: return { transform: 'rotate(0deg)' };
  }
};

export const START_POSITIONS = {
  2: [
    { row: 3, col: 2, orientation: 'N' },
    { row: 3, col: 4, orientation: 'N' }  
  ],
  3: [
    { row: 2, col: 3, orientation: 'N' },
    { row: 3, col: 2, orientation: 'W' },
    { row: 3, col: 4, orientation: 'E' }
  ],
  4: [
    { row: 3, col: 4, orientation: 'N' }, 
    { row: 4, col: 3, orientation: 'W' }, 
    { row: 5, col: 4, orientation: 'S' }, 
    { row: 4, col: 5, orientation: 'E' }  
  ],
  5: [
    { row: 3, col: 3, orientation: 'N' }, 
    { row: 3, col: 5, orientation: 'N' }, 
    { row: 5, col: 3, orientation: 'S' }, 
    { row: 5, col: 5, orientation: 'S' }, 
    { row: 4, col: 6, orientation: 'E' }  
  ],
  6: [
    { row: 4, col: 4, orientation: 'N' }, 
    { row: 4, col: 6, orientation: 'N' }, 
    { row: 6, col: 4, orientation: 'S' }, 
    { row: 6, col: 6, orientation: 'S' }, 
    { row: 5, col: 7, orientation: 'E' },
    { row: 5, col: 3,  orientation: 'w' }
  ],
  7: [
    { row: 3, col: 4,  orientation: 'N' },
    { row: 3, col: 6,  orientation: 'N' },
    { row: 5, col: 4, orientation: 'W' },
    { row: 7, col: 4, orientation: 'S' },
    { row: 7, col: 6, orientation: 'S' },
    { row: 4, col: 7, orientation: 'E' },
    { row: 6, col: 7,  orientation: 'E' } 
  ],
  8: [
    { row: 4, col: 5,  orientation: 'N' },
    { row: 4, col: 7,  orientation: 'N' },
    { row: 5, col: 4, orientation: 'W' },
    { row: 7, col: 4, orientation: 'W' },
    { row: 8, col: 5, orientation: 'S' },
    { row: 8, col: 7, orientation: 'S' },
    { row: 5, col: 8,  orientation: 'E' },
    { row: 7, col: 8,  orientation: 'E' }
  ]
};

export const ENERGY_ACTIONS = [
  {
    id: "BOOST",
    label: "BOOST",
    description: "Spend 1 Energy to play 3 LINE cards instead of 2.",
    summary: "This turn you may place up to 3 LINE cards.",
    cardsToPlace: 3,
    startFromPenultimate: false,
  },
  {
    id: "BRAKE",
    label: "BRAKE",
    description: "Spend 1 Energy to play only 1 LINE card instead of 2.",
    summary: "Limit this turn to a single LINE card.",
    cardsToPlace: 1,
    startFromPenultimate: false,
  },
  {
    id: "REVERSE",
    label: "REVERSE",
    description: "Spend 1 Energy to continue your line from the penultimate LINE card.",
    summary: "You may extend the line starting from the penultimate card placed.",
    cardsToPlace: 2,
    startFromPenultimate: true,
  },
  {
    id: "EXTRA_FUEL",
    label: "EXTRA FUEL",
    description: "Spend 1 Energy to draw an additional LINE card.",
    summary: "You will draw one extra LINE card immediately.",
    cardsToPlace: 2,
    startFromPenultimate: false,
  },
  {
    id: "JUMP_LINE",
    label: "JUMP LINE",
    description: "You can jump over a partner line card (but not continuing his line).",
    summary: "You may jump over a partner line card this turn.",
    cardsToPlace: 1,
    startFromPenultimate: false,
  },
];