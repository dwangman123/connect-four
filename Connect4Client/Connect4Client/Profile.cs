using System;
using System.Collections.Generic;
using System.Text;

namespace Connect4Client
{
    class Profile
    {
        public string username { get; set; }

        public int wins { get; set; }

        public int losses { get; set; }

        public List<Match> history { get; set; }

    }
}
