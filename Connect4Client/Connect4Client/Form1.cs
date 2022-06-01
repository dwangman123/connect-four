using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using System.Windows.Forms;
using System.Text.Json;

namespace Connect4Client
{
    public partial class Form1 : Form
    {
        string userName = "";

        Dictionary<int, List<PictureBox>> board = new Dictionary<int, List<PictureBox>>();
        Button[] cells;

        bool connected = false;
        bool turn = false;
        Socket clientSocket;
        public Form1()
        {
            InitializeComponent();
        }

        private void Form1_Load(object sender, EventArgs e)
        {
            cells = new Button[7];
            for (int i = 0; i < 7; i++)
            {
                board.Add(i, new List<PictureBox>());
                cells[i] = new Button();
                cells[i].Width = 50;
                cells[i].Height = 50;
                cells[i].Text = "Drop";
                cells[i].Font = new Font(FontFamily.GenericSansSerif, 5, FontStyle.Bold);
                cells[i].Location = new Point(250 + (i * 50), 450 - (7 * 50));
                cells[i].Tag = i;
                cells[i].Click += Drop_Click;
                GamePanel.Controls.Add(cells[i]);
                for (int j = 0; j < 6; j++)
                {
                    PictureBox p = new PictureBox();
                    p.Width = 50;
                    p.Height = 50;
                    p.Location = new Point(250 + (i * 50), 450 - (j * 50));
                    p.BorderStyle = BorderStyle.FixedSingle;
                    //p.Image = Properties.Resources.redCircle;
                    p.SizeMode = PictureBoxSizeMode.StretchImage;
                    GamePanel.Controls.Add(p);
                    board[i].Add(p);

                }
            }
            SearchButton.Visible = false;
            textBox1.Visible = false;
            InitializeBoard();
        }

        public void InitializeBoard()
        {
            for (int i = 0; i < 7; i++)
            {
                var list = board[i];
                for (int j = 0; j < list.Count; j++)
                {
                    list[j].Image = null;
                }
            }
        }


        public async Task<int> Test(Socket s)
        {
            connected = false;
            while (true)
            {

                /*byte[] rcvLenBytes = new byte[4];
                s.Receive(rcvLenBytes);
                int rcvLen = System.BitConverter.ToInt32(rcvLenBytes, 0);
                byte[] rcvBytes = new byte[rcvLen];
                s.Receive(rcvBytes);
                String rcv = System.Text.Encoding.ASCII.GetString(rcvBytes);
                //label1.Text = "Client received: " + rcv;
                MethodInvoker inv = delegate
                {
                    this.label1.Text = "Client received: " + rcv;
                    DoSomething(rcv);
                };

                this.Invoke(inv);*/

                if (connected == true)
                {
                    break;
                }
                string toSend = userName;
                int toSendLen = System.Text.Encoding.ASCII.GetByteCount(toSend);
                byte[] toSendBytes = System.Text.Encoding.ASCII.GetBytes(toSend);
                byte[] toSendLenBytes = System.BitConverter.GetBytes(toSendLen);
                s.Send(toSendLenBytes);
                s.Send(toSendBytes);

                Thread.Sleep(10);
            }

            return 0;
        }

        public async Task<int> Receiver(Socket s)
        {
            while (true)
            {
                byte[] rcvLenBytes = new byte[4];
                s.Receive(rcvLenBytes);
                int rcvLen = System.BitConverter.ToInt32(rcvLenBytes, 0);
                byte[] rcvBytes = new byte[rcvLen];
                s.Receive(rcvBytes);
                String rcv = System.Text.Encoding.ASCII.GetString(rcvBytes);
                //label1.Text = "Client received: " + rcv;

                if (rcv.Length > 0)
                {
                    connected = true;
                }

                //if(rcv == "name")
                //{
                //    string toSend = userName;
                //    int toSendLen = System.Text.Encoding.ASCII.GetByteCount(toSend);
                //    byte[] toSendBytes = System.Text.Encoding.ASCII.GetBytes(toSend);
                //    byte[] toSendLenBytes = System.BitConverter.GetBytes(toSendLen);
                //    s.Send(toSendLenBytes);
                //    s.Send(toSendBytes);
                //}

                bool isOver = false;
                if (rcv == "your turn")
                {
                    turn = true;
                    MethodInvoker inv = delegate
                    {
                        SearchingLabel.Visible = false;
                        label3.Text = "Your turn! You have 20 seconds to make a move!";
                        //this.label3.Text = "Client received: " + rcv;
                        //this.button1.Visible = true;
                        for (int i = 0; i < cells.Length; i++)
                        {
                            cells[i].Visible = true;
                            cells[i].Enabled = true;
                        }
                        //this.textBox2.Visible = true;
                        this.label3.Visible = true;
                        this.label4.Visible = true;
                        //DoSomething(rcv); 
                    };

                    this.Invoke(inv);
                }
                else if (rcv == "opponent turn")
                {
                    turn = false;
                    MethodInvoker inv = delegate
                    {
                        //this.label3.Text = "Client received: " + rcv;
                        label3.Text = "Opponent's turn!";
                        //this.button1.Visible = true;
                        for (int i = 0; i < cells.Length; i++)
                        {
                            cells[i].Visible = true;
                            cells[i].Enabled = false;
                        }
                        //this.textBox2.Visible = true;
                        this.label3.Visible = true;
                        this.label4.Visible = true;
                        //DoSomething(rcv); 
                    };

                    this.Invoke(inv);
                }
                else if (rcv.Contains("red"))
                {
                    int result = int.Parse(rcv.Substring("red".Length));
                    MethodInvoker inv = delegate
                    {
                        var list = board[result];
                        int index = 0;
                        for (int i = list.Count - 1; i >= 0; i--)
                        {
                            if (list[i].Image == null)
                            {
                                index = i;
                            }
                        }
                        label4.Text = "";
                        list[index].Image = Properties.Resources.redCircle;
                    };

                    this.Invoke(inv);
                }
                else if (rcv.Contains("yellow"))
                {
                    int result = int.Parse(rcv.Substring("yellow".Length));

                    MethodInvoker inv = delegate
                    {
                        var list = board[result];
                        int index = 0;
                        for (int i = list.Count - 1; i >= 0; i--)
                        {
                            if (list[i].Image == null)
                            {
                                index = i;
                            }
                        }
                        label4.Text = "";
                        list[index].Image = Properties.Resources.yellowCircle;
                    };

                    this.Invoke(inv);
                }
                else
                {
                    if (rcv == "you win" || rcv == "you lose")
                    {
                        turn = false;
                        isOver = true;

                    }
                    MethodInvoker inv2 = delegate
                    {
                        if (rcv == "you win")
                        {
                            label4.Text = "You Won!";
                            ContinueButton.Visible = true;
                        }
                        else if (rcv == "you lose")
                        {
                            label4.Text = "You Lost!";
                            ContinueButton.Visible = true;
                        }
                        else if (rcv == "opponent timed out")
                        {
                            label4.Text = "Opponent timed out!";
                            ContinueButton.Visible = true;
                        }
                        else if (rcv == "invalid")
                        {
                            label4.Text = "That move was invalid";
                        }
                        else
                        {
                            label4.Text = "";
                        }
                        //this.label4.Text = "Client received: " + rcv;
                        //DoSomething(rcv); 
                    };

                    this.Invoke(inv2);
                }

                if (turn == true)
                {
                    MethodInvoker inv2 = delegate
                    {
                        for (int i = 0; i < cells.Length; i++)
                        {
                            cells[i].Enabled = true;
                        }
                        //DoSomething(rcv); 
                    };
                    this.Invoke(inv2);
                }
                else
                {
                    MethodInvoker inv2 = delegate
                    {
                        for (int i = 0; i < cells.Length; i++)
                        {
                            cells[i].Enabled = false;
                        }
                        //DoSomething(rcv); 
                    };
                    this.Invoke(inv2);
                }

                if (isOver)
                {
                    clientSocket.Close();
                    return 1;
                }
            }

            return 0;
        }
        Task<Task> t;
        Task<Task> t2;
        private void textBox1_TextChanged(object sender, EventArgs e)
        {
            userName = textBox1.Text;
        }

        private void SearchButton_Click(object sender, EventArgs e)
        {
            try
            {
                IPEndPoint serverAddress = new IPEndPoint(IPAddress.Parse("127.0.0.1"), 4343);

                clientSocket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
                clientSocket.Connect(serverAddress);
                MethodInvoker inv = delegate
                {
                    connected = false;
                };

                this.Invoke(inv);
                Thread.Sleep(10);

                t = Task.Factory.StartNew(async () => { await Test(clientSocket); });

                t2 = Task.Factory.StartNew(async () => { await Receiver(clientSocket); });
                SearchButton.Visible = false;
                textBox1.Visible = false;
            }
            catch (Exception ex)
            {
                MessageBox.Show("Could not connect to server.");
            }

        }

        private void button1_Click(object sender, EventArgs e)
        {
            if (turn)
            {
                string toSend = textBox2.Text;
                int toSendLen = System.Text.Encoding.ASCII.GetByteCount(toSend);
                byte[] toSendBytes = System.Text.Encoding.ASCII.GetBytes(toSend);
                byte[] toSendLenBytes = System.BitConverter.GetBytes(toSendLen);
                clientSocket.Send(toSendLenBytes);
                clientSocket.Send(toSendBytes);
            }
        }
        private void Drop_Click(object sender, EventArgs e)
        {
            if (turn)
            {
                Button b = sender as Button;
                //textBox2.Text = b.Tag.ToString();
                //button1_Click(sender, e);
                string toSend = b.Tag.ToString(); //"this is a test";//"drop" + b.Tag.ToString();
                int toSendLen = System.Text.Encoding.ASCII.GetByteCount(toSend);
                byte[] toSendBytes = System.Text.Encoding.ASCII.GetBytes(toSend);
                byte[] toSendLenBytes = System.BitConverter.GetBytes(toSendLen);
                clientSocket.Send(toSendLenBytes);
                clientSocket.Send(toSendBytes);
            }
        }

        private void LoginButton_Click(object sender, EventArgs e)
        {
            string u = userNameTextBox.Text;
            string p = PasswordTextBox.Text;
            if (!string.IsNullOrWhiteSpace(p) && !string.IsNullOrWhiteSpace(u))
            {
                try
                {
                    IPEndPoint serverAddress = new IPEndPoint(IPAddress.Parse("127.0.0.1"), 4344);

                    Socket clientSocket2 = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
                    clientSocket2.Connect(serverAddress);
                    string toSend = u;
                    int toSendLen = System.Text.Encoding.ASCII.GetByteCount(toSend);
                    byte[] toSendBytes = System.Text.Encoding.ASCII.GetBytes(toSend);
                    byte[] toSendLenBytes = System.BitConverter.GetBytes(toSendLen);
                    clientSocket2.Send(toSendLenBytes);
                    clientSocket2.Send(toSendBytes);
                    toSend = p;
                    toSendLen = System.Text.Encoding.ASCII.GetByteCount(toSend);
                    toSendBytes = System.Text.Encoding.ASCII.GetBytes(toSend);
                    toSendLenBytes = System.BitConverter.GetBytes(toSendLen);
                    clientSocket2.Send(toSendLenBytes);
                    clientSocket2.Send(toSendBytes);

                    byte[] rcvLenBytes = new byte[4];
                    clientSocket2.Receive(rcvLenBytes);
                    int rcvLen = System.BitConverter.ToInt32(rcvLenBytes, 0);
                    byte[] rcvBytes = new byte[rcvLen];
                    clientSocket2.Receive(rcvBytes);
                    String rcv = System.Text.Encoding.ASCII.GetString(rcvBytes);
                    clientSocket2.Close();
                    bool result = false;
                    if (rcv == "true")
                    {
                        result = true;
                    }
                    userNameTextBox.Clear();
                    PasswordTextBox.Clear();

                    if (result == true)
                    {
                        userName = u;
                        enterProfile();
                    }
                    else
                    {
                        loginErrorLabel.Text = "Invalid credentials entered.  Please try again.";
                    }

                }
                catch (Exception ex)
                {
                    MessageBox.Show("Could not connect to server.");
                }
            }
            else
            {
                loginErrorLabel.Text = "Username and Password must contain a value that is not only spaces.";
            }
        }

        private void LoginGuestButton_Click(object sender, EventArgs e)
        {
            userName = "guest";
            searchForMatch();
        }

        public void searchForMatch()
        {
            GamePanel.Visible = true;
            RegisterPanel.Visible = false;
            LoginPanel.Visible = false;
            ProfilePanel.Visible = false;
            InitializeBoard();
            try
            {
                IPEndPoint serverAddress = new IPEndPoint(IPAddress.Parse("127.0.0.1"), 4343);

                clientSocket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
                clientSocket.Connect(serverAddress);

                t = Task.Factory.StartNew(async () => { await Test(clientSocket); });

                t2 = Task.Factory.StartNew(async () => { await Receiver(clientSocket); });
                SearchingLabel.Visible = true;
                label3.Text = "";
                label4.Text = "";
                ContinueButton.Visible = false;
            }

            catch (Exception ex)
            {
                MessageBox.Show("Could not connect to server.");
            }
        }
        public void enterProfile()
        {
            LoginPanel.Visible = false;
            RegisterPanel.Visible = false;
            GamePanel.Visible = false;
            LeaderBoardPanel.Visible = false;
            ProfilePanel.Visible = true;
            WinCountLabel.Text = "";
            WelcomeLabel.Text = "Welcome " + userName + "!";
            try
            {
                IPEndPoint serverAddress = new IPEndPoint(IPAddress.Parse("127.0.0.1"), 4347);

                Socket clientSocket2 = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
                clientSocket2.Connect(serverAddress);

                string toSend = userName;
                int toSendLen = System.Text.Encoding.ASCII.GetByteCount(toSend);
                byte[] toSendBytes = System.Text.Encoding.ASCII.GetBytes(toSend);
                byte[] toSendLenBytes = System.BitConverter.GetBytes(toSendLen);
                clientSocket2.Send(toSendLenBytes);
                clientSocket2.Send(toSendBytes);

                byte[] rcvLenBytes = new byte[4];
                clientSocket2.Receive(rcvLenBytes);
                int rcvLen = System.BitConverter.ToInt32(rcvLenBytes, 0);
                byte[] rcvBytes = new byte[rcvLen];
                clientSocket2.Receive(rcvBytes);
                String rcv = System.Text.Encoding.ASCII.GetString(rcvBytes);

                Profile p = JsonSerializer.Deserialize<Profile>(rcv);
                p.history.Reverse();
                var bindingList = new BindingList<Match>(p.history);
                var source = new BindingSource(bindingList, null);
                dataGridView4.DataSource = source;


                WinCountLabel.Text = "Wins: " + p.wins + " Losses: " + p.losses;
                clientSocket2.Close();
            }
            catch (Exception ex)
            {
                MessageBox.Show("Could not connect to server.");
            }
        }
        public void enterlogin()
        {
            LoginPanel.Visible = true;
            RegisterPanel.Visible = false;
            GamePanel.Visible = false;
            ProfilePanel.Visible = false;
            LeaderBoardPanel.Visible = false;
            loginErrorLabel.Text = "";
            userNameTextBox.Clear();
            PasswordTextBox.Clear();

        }

        public void enterLeaderBoard()
        {
            LoginPanel.Visible = false;
            RegisterPanel.Visible = false;
            GamePanel.Visible = false;
            ProfilePanel.Visible = false;
            LeaderBoardPanel.Visible = true;
            try
            {
                IPEndPoint serverAddress = new IPEndPoint(IPAddress.Parse("127.0.0.1"), 4346);

                Socket clientSocket2 = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
                clientSocket2.Connect(serverAddress);
                byte[] rcvLenBytes = new byte[4];
                clientSocket2.Receive(rcvLenBytes);
                int rcvLen = System.BitConverter.ToInt32(rcvLenBytes, 0);
                byte[] rcvBytes = new byte[rcvLen];
                clientSocket2.Receive(rcvBytes);
                String rcv = System.Text.Encoding.ASCII.GetString(rcvBytes);

                List<List<Player>> players = JsonSerializer.Deserialize<List<List<Player>>>(rcv);
                var bindingList = new BindingList<Player>(players[0]);
                var source = new BindingSource(bindingList, null);
                dataGridView2.DataSource = source;

                var bindingList1 = new BindingList<Player>(players[1]);
                var source1 = new BindingSource(bindingList1, null);
                dataGridView1.DataSource = source1;
                clientSocket2.Close();
            }
            catch (Exception ex)
            {
                MessageBox.Show("Could not connect to server.");
            }
        }

        private void RegisterButton_Click(object sender, EventArgs e)
        {
            LoginPanel.Visible = false;
            RegisterPanel.Visible = true;
            RePasswordTextBox.Clear();
            passwordRegTextBox.Clear();
            usernameRegTextBox.Clear();
            RegisterError.Text = "";
        }

        private void BackToLoginButton_Click(object sender, EventArgs e)
        {
            RegisterPanel.Visible = false;
            enterlogin();
        }

        private void button2_Click(object sender, EventArgs e)
        {
            string u = usernameRegTextBox.Text;
            string p = passwordRegTextBox.Text;
            string rp = RePasswordTextBox.Text;
            if (!String.IsNullOrWhiteSpace(u) && !String.IsNullOrWhiteSpace(p) && !String.IsNullOrWhiteSpace(rp))
            {
                if (p != rp)
                {
                    RegisterError.Text = "Passwords did not match";
                }
                else//otherwise call the signup on the server
                {
                    bool result = false;
                    try
                    {
                        IPEndPoint serverAddress = new IPEndPoint(IPAddress.Parse("127.0.0.1"), 4345);

                        Socket clientSocket2 = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
                        clientSocket2.Connect(serverAddress);
                        string toSend = u;
                        int toSendLen = System.Text.Encoding.ASCII.GetByteCount(toSend);
                        byte[] toSendBytes = System.Text.Encoding.ASCII.GetBytes(toSend);
                        byte[] toSendLenBytes = System.BitConverter.GetBytes(toSendLen);
                        clientSocket2.Send(toSendLenBytes);
                        clientSocket2.Send(toSendBytes);
                        toSend = p;
                        toSendLen = System.Text.Encoding.ASCII.GetByteCount(toSend);
                        toSendBytes = System.Text.Encoding.ASCII.GetBytes(toSend);
                        toSendLenBytes = System.BitConverter.GetBytes(toSendLen);
                        clientSocket2.Send(toSendLenBytes);
                        clientSocket2.Send(toSendBytes);

                        byte[] rcvLenBytes = new byte[4];
                        clientSocket2.Receive(rcvLenBytes);
                        int rcvLen = System.BitConverter.ToInt32(rcvLenBytes, 0);
                        byte[] rcvBytes = new byte[rcvLen];
                        clientSocket2.Receive(rcvBytes);
                        String rcv = System.Text.Encoding.ASCII.GetString(rcvBytes);
                        clientSocket2.Close();
                        if (rcv == "true")
                        {
                            result = true;
                        }

                        if (result == true)
                        {
                            RegisterError.Text = "Successfully created account!  Click Back to Login.";

                            RePasswordTextBox.Clear();
                            passwordRegTextBox.Clear();
                            usernameRegTextBox.Clear();
                        }
                        else
                        {
                            RegisterError.Text = "Username already exists";
                        }
                    }
                    catch (Exception ex)
                    {
                        MessageBox.Show("Could not connect to server.");
                    }
                }

            }
            else
            {
                RegisterError.Text = "Once of the fields was not entered or was whitespace.";
            }
        }

        private void SignOutButton_Click(object sender, EventArgs e)
        {
            enterlogin();
            userName = "";
        }

        private void PlayButton_Click(object sender, EventArgs e)
        {
            searchForMatch();
        }

        private void ViewLeaderBoardButton_Click(object sender, EventArgs e)
        {
            enterLeaderBoard();
        }

        private void BackButtonLeaderBoard_Click(object sender, EventArgs e)
        {
            enterProfile();
        }

        private void ContinueButton_Click(object sender, EventArgs e)
        {
            if (userName.ToLower() != "guest")
            {
                enterProfile();
            }
            else
            {
                enterlogin();
            }
        }
    }
}
